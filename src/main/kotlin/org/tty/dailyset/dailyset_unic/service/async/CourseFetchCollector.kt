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
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.MessageIntent
import org.tty.dailyset.dailyset_unic.bean.Responses
import org.tty.dailyset.dailyset_unic.bean.converters.*
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.enums.MessageTopics
import org.tty.dailyset.dailyset_unic.bean.enums.PeriodCode
import org.tty.dailyset.dailyset_unic.bean.enums.PythonInteractActionType
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus
import org.tty.dailyset.dailyset_unic.bean.interact.PythonResponseCode
import org.tty.dailyset.dailyset_unic.bean.interact.PythonResponseCode.loginFail
import org.tty.dailyset.dailyset_unic.bean.interact.PythonResponseCode.success
import org.tty.dailyset.dailyset_unic.bean.interact.PythonResponseCode.unknown
import org.tty.dailyset.dailyset_unic.bean.resp.PythonCourseResp
import org.tty.dailyset.dailyset_unic.component.EncryptProvider
import org.tty.dailyset.dailyset_unic.component.EnvironmentVars
import org.tty.dailyset.dailyset_unic.intent.MessagePostIntent
import org.tty.dailyset.dailyset_unic.service.*
import org.tty.dailyset.dailyset_unic.util.Diff
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
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
    private lateinit var dailySetService: DailySetService

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
        // 获取当前的版本号
        val currentVersion = preferenceService.unicCourseCurrentVersion
        getCoroutineScope().launch {
            var retryCount = 0
            while (doPushTaskOfNewTicket(unicTicket, retryCount, currentVersion)) {
                delay(5000)
                retryCount += 1
            }
        }
    }

    fun pushScheduleTask() {
        // 获取当前的版本号
        val currentVersion = preferenceService.unicCourseCurrentVersion
        val yearPeriod = preferenceService.realYearPeriodNow
        if (yearPeriod.periodCode != PeriodCode.FirstTerm && yearPeriod.periodCode != PeriodCode.SecondTerm) {
            logger.info("[schedule] current is not in first or second term, skip schedule task.")
            return
        }

        val now = LocalDateTime.now()
        logger.info("[schedule] schedule task > ${now.toStandardString()}")
        getCoroutineScope().launch {
            doScheduleTask(currentVersion)
            logger.info("[schedule] schedule task < ${now.toStandardString()} - ${LocalDateTime.now().toStandardString()}")
        }
    }

    private suspend fun doPushTaskOfNewTicket(unicTicket: UnicTicket, retryCount: Int = 0, currentVersion: Int): Boolean {
        // construct the input args
        val actionType = PythonInteractActionType.Initialize
        val decryptedPassword = encryptProvider.aesDecrypt(unicTicket.uid, unicTicket.password)
        @Suppress("FoldInitializerAndIfToElvis")
        if (decryptedPassword == null) {
            // TODO: push message: password failure
            messageService.sendTicketMessage(unicTicket, MessageTopics.passwordFail, "password failure")
            return false
        }
        val yearPeriod = preferenceService.realYearPeriodNow

        updateTickStatus(unicTicket.ticketId, UnicTicketStatus.Initialized)

        // call the python interact service and get the response
        val result = callPythonScriptGetCourse(unicTicket.uid, decryptedPassword, actionType, yearPeriod.year, yearPeriod.periodCode.toTerm())
        // TODO: debug only.
        //val result = mockGetCourseWithFile()
        return when (result.code) {
            PythonResponseCode.success -> {
                doPostTask(unicTicket, actionType, result, currentVersion)
                updateTickStatus(unicTicket.ticketId, UnicTicketStatus.Checked)
                messageService.sendTicketMessage(unicTicket, MessageTopics.ok, "get initialize info success.")
                false
            }
            PythonResponseCode.loginFail -> {

                updateTickStatus(unicTicket.ticketId, UnicTicketStatus.LoginFailure)
                messageService.sendTicketMessage(unicTicket, MessageTopics.loginFail, "login failure.")
                false
            }
            else -> {
                messageService.sendTicketMessage(unicTicket, MessageTopics.unknownError, "unknown error. at retryCount: $retryCount")
                val next = retryCount < preferenceService.unicCourseFetchRetryTimes
                if (!next) {
                    updateTickStatus(unicTicket.ticketId, UnicTicketStatus.UnknownFailure)
                }
                return next
            }
        }
    }

    private suspend fun doScheduleTask(currentVersion: Int) {
        val tickets = ticketService.findAllUnicTicketByAvailableStatus()
        val group = tickets.groupBy { it.uid }

        group.forEach { (t, u) ->
            doScheduleTaskWithUid(t, u, currentVersion)
        }
    }

    private suspend fun doScheduleTaskWithUid(uid: String, tickets: List<UnicTicket>, currentVersion: Int) {
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

        // send the message if ticket status changed.
        val diff = Diff<UnicTicket, UnicTicket, String> {
            source = tickets
            target = updatedTickets
            sourceKeySelector = { it.ticketId }
            targetKeySelector = { it.ticketId }
        }

        val changedTickets = diff.sames.filter {
            it.source.status != it.target.status
        }.map { it.target }

        changedTickets.forEach {
            messageService.sendTicketMessage(it, 0, "status changed to: ${it.status}")
        }

        if (success) {
            doPostTask(successTicket!!, actionType = PythonInteractActionType.GetCourse, result = result, currentVersion = currentVersion)
        }
    }

    private suspend fun updateTickStatus(ticketId: String, ticketStatus: UnicTicketStatus) {
        ticketService.updateTicketStatus(ticketId, ticketStatus)
    }

    private suspend fun doPostTask(unicTicket: UnicTicket, actionType: PythonInteractActionType, result: Responses<PythonCourseResp>, currentVersion: Int) {
        // update the studentInfo
        checkNotNull(result.data) { "the result data is null" }
        dailySetService.updateOrInsertDailySetStudentInfoMeta(result.data.userInfo.toDailySetStudentInfoMeta())
        dailySetService.ensureStudentDailySetCreated(result.data.userInfo.studentNumber)

        logger.info(">>doPostTask(${unicTicket.uid}):${actionType.value}")
        // find the related year terms
        val yearPeriods = result.data.yearPeriods()
        var addCount = 0
        var removeCount = 0

        for (yearPeriod in yearPeriods) {
            val r = dailySetService.updateWithSource(
                unicTicket.uid,
                courses = result.data.select(yearPeriod).toDailySetCourses().asIterable(),
                yearPeriod = yearPeriod,
                currentVersion = currentVersion
            )
            addCount += r.addCount
            removeCount += r.removeCount
        }
        dailySetService.updateSourceVersion(result.data.userInfo.studentNumber, currentVersion)

        logger.info("<<doPostTask(${unicTicket.uid}):${actionType.value}+$addCount-$removeCount")

        // send the message
        val uid = unicTicket.uid
        val tickets = ticketService.findAllUnicTicketByUidAndOkStatus(uid)

        messageService.sendTicketMessage(MessagePostIntent(
            tickets.map { it.ticketId },
            MessageIntent(
                topic = MessageTopics.dailySetUnicCourse,
                referer = MessageTopics.referer,
                code = MessageTopics.ok,
                content = "课程表信息已经修改."
            )
        ))

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

}