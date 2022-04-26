/**
 * create at 2022/4/24
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.service.async

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.Responses
import org.tty.dailyset.dailyset_unic.bean.converters.select
import org.tty.dailyset.dailyset_unic.bean.converters.toCourseSequence
import org.tty.dailyset.dailyset_unic.bean.converters.toUnicStudentInfo
import org.tty.dailyset.dailyset_unic.bean.converters.yearPeriods
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.enums.PythonInteractActionType
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus
import org.tty.dailyset.dailyset_unic.bean.enums.UpdateCode
import org.tty.dailyset.dailyset_unic.bean.interact.PythonResponseCode
import org.tty.dailyset.dailyset_unic.bean.interact.PythonResponseCode.loginFail
import org.tty.dailyset.dailyset_unic.bean.interact.PythonResponseCode.success
import org.tty.dailyset.dailyset_unic.bean.interact.PythonResponseCode.unknown
import org.tty.dailyset.dailyset_unic.bean.resp.PythonCourseResp
import org.tty.dailyset.dailyset_unic.component.EncryptProvider
import org.tty.dailyset.dailyset_unic.component.EnvironmentVars
import org.tty.dailyset.dailyset_unic.service.MessageService
import org.tty.dailyset.dailyset_unic.service.PreferenceService
import org.tty.dailyset.dailyset_unic.service.TicketService
import org.tty.dailyset.dailyset_unic.service.UnicStudentAndCourseService
import java.io.BufferedReader
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext

@Component
class CourseFetchCollector {

    private var newFetchCoroutineScope: CoroutineScope? = null

    @Autowired
    private lateinit var environmentVars: EnvironmentVars

    @Autowired
    private lateinit var encryptProvider: EncryptProvider

    @Autowired
    private lateinit var preferenceService: PreferenceService

    @Autowired
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var ticketService: TicketService

    @Autowired
    private lateinit var unicStudentAndCourseService: UnicStudentAndCourseService

    private val os = System.getProperty("os.name").lowercase(Locale.getDefault())

    private val logger = LoggerFactory.getLogger(CourseFetchCollector::class.java)

    fun getCoroutineScope(): CoroutineScope {
        if (newFetchCoroutineScope == null) {
            // normal task is run on current scheduler.
            newFetchCoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Default + SupervisorJob())
        }
        return newFetchCoroutineScope!!
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    fun scheduledFetchTask() {
        logger.info("[**scheduleTask**] start")
    }

    fun pushTaskOfNewTicket(unicTicket: UnicTicket) {
        getCoroutineScope().launch {
            var retryCount = 0
            while (doPushTaskOfNewTicket(unicTicket, retryCount)) {
                delay(5000)
                retryCount += 1
            }
        }
    }

    suspend fun doPushTaskOfNewTicket( unicTicket: UnicTicket, retryCount: Int = 0): Boolean {
        // construct the input args
        val actionType = PythonInteractActionType.Initialize
        val decryptedPassword = encryptProvider.aesDecrypt(unicTicket.uid, unicTicket.password)
        @Suppress("FoldInitializerAndIfToElvis")
        if (decryptedPassword == null) {
            // TODO: push message: password failure
            messageService.sendTicketMessage(unicTicket, 1, "password failure")
            return false
        }
        val year = preferenceService.unicCurrentCourseYear
        val term = preferenceService.unicCurrentCourseTerm

        // call the python interact service and get the response
        //val result = callPythonScriptGetCourse(unicTicket.uid, decryptedPassword, actionType, year, term)
        // TODO: debug only.
        val result = mockGetCourseWithFile()
        return when (result.code) {
            PythonResponseCode.success -> {
                messageService.sendTicketMessage(unicTicket, 0, "get initialize info success.")
                doPostTask(unicTicket, actionType, result)
                updateTickStatus(unicTicket.ticketId, UnicTicketStatus.Checked)
                false
            }
            PythonResponseCode.loginFail -> {
                messageService.sendTicketMessage(unicTicket, 1, "login failure.")
                updateTickStatus(unicTicket.ticketId, UnicTicketStatus.Failure)
                false
            }
            else -> {
                messageService.sendTicketMessage(unicTicket, 1, "unknown error. at retryCount: $retryCount")
                val next = retryCount < preferenceService.unicCourseFetchRetryTimes
                if (!next) {
                    updateTickStatus(unicTicket.ticketId, UnicTicketStatus.Failure)
                }
                return next
            }
        }
    }

    suspend fun updateTickStatus(ticketId: String, ticketStatus: UnicTicketStatus) {
        ticketService.updateTicketStatus(ticketId, ticketStatus)
    }

    suspend fun doPostTask(unicTicket: UnicTicket, actionType: PythonInteractActionType, result: Responses<PythonCourseResp>) {
        // update the studentInfo
        checkNotNull(result.data) { "the result data is null" }
        unicStudentAndCourseService.updateUnicStudentInfo(result.data.userInfo.toUnicStudentInfo())

        logger.info(">>doPostTask(${unicTicket.uid}):${actionType.value}")
        // find the related year terms
        val yearPeriods = result.data.yearPeriods()
        var addCount: Int = 0
        var removeCount: Int = 0

        for (yearPeriod in yearPeriods) {
            val r = unicStudentAndCourseService.updateWithSource(
                unicTicket.uid,
                courses = result.data.select(yearPeriod).toCourseSequence().asIterable(),
                yearPeriod = yearPeriod
            )
            addCount += r.data.count { it.updateCode == UpdateCode.Added }
            removeCount += r.data.count { it.updateCode == UpdateCode.Removed }
        }

        logger.info("<<doPostTask(${unicTicket.uid}):${actionType.value}+$addCount-$removeCount")

    }


    suspend fun callPythonScriptGetCourse(uid: String, password: String, actionType: PythonInteractActionType, year: Int, term: Int): Responses<PythonCourseResp>
    = withContext(Dispatchers.IO) {
        var reader: BufferedReader? = null
        val result: Responses<PythonCourseResp> = try {
            val inputArgs = arrayOf(
                environmentVars.pythonPath,
                environmentVars.scriptPath,
                uid,
                password,
                actionType.value,
                year.toString(),
                term.toString()
            )
            val process = Runtime.getRuntime().exec(inputArgs)
            val charset = if (os.indexOf("windows") >= 0) {
                Charset.forName("gbk")
            } else {
                Charset.forName("utf-8")
            }
            reader = process.inputStream.bufferedReader(charset)
            val text = reader.readText()
            logger.info("python script output: $text")
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            val resultEntity = json.decodeFromString<Responses<PythonCourseResp>>(text)
            resultEntity
        } catch (e: Exception) {
            e.printStackTrace()
            Responses.fail(code = PythonResponseCode.unknown, message = e.message!!)
        } finally {
            reader?.close()
        }

        return@withContext result
    }

    @Value("classpath:result.json")
    private lateinit var resultFile: File

    suspend fun mockGetCourseWithFile(): Responses<PythonCourseResp> = withContext(Dispatchers.IO) {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        return@withContext json.decodeFromString<Responses<PythonCourseResp>>(resultFile.readText())
    }
}