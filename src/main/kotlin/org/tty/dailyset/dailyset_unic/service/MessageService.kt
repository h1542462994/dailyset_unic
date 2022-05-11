package org.tty.dailyset.dailyset_unic.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.MessageIntent
import org.tty.dailyset.dailyset_unic.bean.enums.MessageTopics
import org.tty.dailyset.dailyset_unic.http.DailySetCloudApi
import org.tty.dailyset.dailyset_unic.http.req.MessagePostReq
import org.tty.dailyset.dailyset_unic.intent.MessagePostIntent


@Service
class MessageService {
    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    @Value("\${dailyset.env.message.secret}")
    private lateinit var cloudSecret: String

    @Autowired
    private lateinit var dailySetCloudApi: DailySetCloudApi

    suspend fun sendTicketMessage(messagePostIntent: MessagePostIntent) {
        try {
            dailySetCloudApi.messagePostTicket(MessagePostReq(
                secret = cloudSecret,
                targets = messagePostIntent.targets,
                intent = messagePostIntent.intent
            ))
            logger.info(messagePostIntent.toString())
        } catch (e: Exception) {
            logger.error("send ticket message failed, error: ", e)
        }
    }


    suspend fun sendTicketMessage(unicTicket: UnicTicket, code: Int, message: String) {
        sendTicketMessage(
            MessagePostIntent(targets = listOf(unicTicket.ticketId), intent = MessageIntent(
                topic = MessageTopics.dailySetUnicTicket,
                referer = MessageTopics.referer,
                code = code,
                content = message
            ))
        )
    }

}