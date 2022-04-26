/**
 * create at 2022/4/24
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.service.async

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.Responses
import org.tty.dailyset.dailyset_unic.bean.converters.*
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.enums.PeriodCode
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
import org.tty.dailyset.dailyset_unic.service.UnicCourseComplexService
import java.io.BufferedReader
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.util.*
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
    private lateinit var unicStudentAndCourseService: UnicCourseComplexService

    private val os = System.getProperty("os.name").lowercase(Locale.getDefault())

    private val logger = LoggerFactory.getLogger(CourseFetchCollector::class.java)

    private fun getCoroutineScope(): CoroutineScope {
        if (newFetchCoroutineScope == null) {
            // normal task is run on current scheduler.
            newFetchCoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Default + SupervisorJob())
        }
        return newFetchCoroutineScope!!
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

    fun pushScheduleTask() {
        val yearPeriod = preferenceService.realYearPeriodNow
        if (yearPeriod.periodCode != PeriodCode.FirstTerm && yearPeriod.periodCode != PeriodCode.SecondTerm) {
            logger.info("[schedule] current is not in first or second term, skip schedule task.")
            return
        }

        val now = LocalDateTime.now()
        logger.info("[schedule] schedule task > ${now.toStandardString()}")
        getCoroutineScope().launch {
            doScheduleTask()
            logger.info("[schedule] schedule task < ${now.toStandardString()} - ${LocalDateTime.now().toStandardString()}")
        }
    }

    private suspend fun doPushTaskOfNewTicket(unicTicket: UnicTicket, retryCount: Int = 0): Boolean {
        // construct the input args
        val actionType = PythonInteractActionType.Initialize
        val decryptedPassword = encryptProvider.aesDecrypt(unicTicket.uid, unicTicket.password)
        @Suppress("FoldInitializerAndIfToElvis")
        if (decryptedPassword == null) {
            // TODO: push message: password failure
            messageService.sendTicketMessage(unicTicket, 1, "password failure")
            return false
        }
        val yearPeriod = preferenceService.realYearPeriodNow

        // call the python interact service and get the response
        val result = callPythonScriptGetCourse(unicTicket.uid, decryptedPassword, actionType, yearPeriod.year, yearPeriod.periodCode.toTerm())
        // TODO: debug only.
        //val result = mockGetCourseWithFile()
        return when (result.code) {
            PythonResponseCode.success -> {
                messageService.sendTicketMessage(unicTicket, 0, "get initialize info success.")
                doPostTask(unicTicket, actionType, result)
                updateTickStatus(unicTicket.ticketId, UnicTicketStatus.Checked)
                false
            }
            PythonResponseCode.loginFail -> {
                messageService.sendTicketMessage(unicTicket, 1, "login failure.")
                updateTickStatus(unicTicket.ticketId, UnicTicketStatus.LoginFailure)
                false
            }
            else -> {
                messageService.sendTicketMessage(unicTicket, 1, "unknown error. at retryCount: $retryCount")
                val next = retryCount < preferenceService.unicCourseFetchRetryTimes
                if (!next) {
                    updateTickStatus(unicTicket.ticketId, UnicTicketStatus.UnknownFailure)
                }
                return next
            }
        }
    }

    private suspend fun doScheduleTask() {
        val tickets = ticketService.findUnicTicketsByAvailableStatus()
        val group = tickets.groupBy { it.uid }

        group.forEach { (t, u) ->
            doScheduleTaskWithUid(t, u)
        }
    }

    private suspend fun doScheduleTaskWithUid(uid: String, tickets: List<UnicTicket>) {
        // get the current year period
        val yearPeriod = preferenceService.realYearPeriodNow
        val reliableTicket = tickets.find { it.status == UnicTicketStatus.Checked.value }
        var success = false
        lateinit var result: Responses<PythonCourseResp>
        var successTicket: UnicTicket? = null
        val updatedTickets = mutableListOf<UnicTicket>()
        val remainTickets = tickets.toMutableList()

        suspend fun withTicket(ticket: UnicTicket): Boolean {
            val decryptedPassword = encryptProvider.aesDecrypt(uid, ticket.password)

            if (decryptedPassword == null) {
                updatedTickets.addAll(remainTickets.filter { it.password == ticket.password }.map { it.copy(UnicTicketStatus.LoginFailure.value) })
                remainTickets.removeAll { it.password == ticket.password }
                return false
            }

            val retryCount = 0
            while (retryCount <= preferenceService.unicCourseFetchRetryTimes) {
                val r = callPythonScriptGetCourse(ticket.uid, decryptedPassword, PythonInteractActionType.GetCourse, yearPeriod.year, yearPeriod.periodCode.toTerm())

                if (r.code == PythonResponseCode.success) {
                    success = true
                    successTicket = ticket
                    updatedTickets.addAll(remainTickets.filter { it.password == ticket.password }.map { it.copy(UnicTicketStatus.Checked.value) })
                    remainTickets.removeAll { it.password == ticket.password }
                    result = r
                    return true
                } else if (r.code == PythonResponseCode.loginFail) {
                    updatedTickets.addAll(remainTickets.filter { it.password == ticket.password }.map { it.copy(UnicTicketStatus.LoginFailure.value) })
                    remainTickets.removeAll { it.password == ticket.password }
                    return false
                } else if (r.code == PythonResponseCode.unknown) {
                    delay(5000)
                    if (retryCount == preferenceService.unicCourseFetchRetryTimes) {
                        updatedTickets.addAll(remainTickets.filter { it.password == ticket.password }.map { it.copy(UnicTicketStatus.UnknownFailure.value) })
                        remainTickets.removeAll { it.password == ticket.password }
                        return false
                    } else {
                        continue
                    }
                }
            }
            return false
        }

        if (reliableTicket != null) {
            withTicket(reliableTicket)
        }

        while (!success && remainTickets.isNotEmpty()) {
            val ticket = remainTickets.first()
            withTicket(ticket)
        }

        if (remainTickets.isNotEmpty()) {
            updatedTickets.addAll(remainTickets.map { it.copy(UnicTicketStatus.LoginFailure.value) })
        }

        ticketService.updateTicketStatusBatch(updatedTickets)


        if (success) {
            doPostTask(successTicket!!, actionType = PythonInteractActionType.GetCourse, result = result)
        }
    }

    private suspend fun updateTickStatus(ticketId: String, ticketStatus: UnicTicketStatus) {
        ticketService.updateTicketStatus(ticketId, ticketStatus)
    }

    private suspend fun doPostTask(unicTicket: UnicTicket, actionType: PythonInteractActionType, result: Responses<PythonCourseResp>) {
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


    private suspend fun callPythonScriptGetCourse(uid: String, password: String, actionType: PythonInteractActionType, year: Int, term: Int): Responses<PythonCourseResp>
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

    private suspend fun mockGetCourseWithFile(): Responses<PythonCourseResp> = withContext(Dispatchers.IO) {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        return@withContext json.decodeFromString<Responses<PythonCourseResp>>(resultFile.readText())
    }
}