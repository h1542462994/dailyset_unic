package org.tty.dailyset.dailyset_unic.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.intent.MessageSendIntent


@Service
class MessageService {
    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    fun sendMessage(messageSendIntent: MessageSendIntent) {
        logger.info(messageSendIntent.toString())
    }

    fun sendTicketMessage(unicTicket: UnicTicket, code: Int, message: String) {
        sendMessage(MessageSendIntent(
            topic = "dailyset_unic_ticket",
            referer = unicTicket.ticketId,
            code = code,
            message = message
        ))
    }

}