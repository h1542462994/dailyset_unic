package org.tty.dailyset.dailyset_unic.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.annotation.DbDirect
import org.tty.dailyset.dailyset_unic.bean.converters.toGrpcTicket
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus
import org.tty.dailyset.dailyset_unic.component.EncryptProvider
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.TicketResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketRequest
import org.tty.dailyset.dailyset_unic.grpc.TicketResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketServiceCoroutineGrpc
import org.tty.dailyset.dailyset_unic.mapper.UnicTicketMapper
import org.tty.dailyset.dailyset_unic.util.uuid

@Component
class TicketService: TicketServiceCoroutineGrpc.TicketServiceImplBase() {

    @Autowired
    private lateinit var encryptProvider: EncryptProvider

    @Autowired
    private lateinit var unicTicketMapper: UnicTicketMapper

    override suspend fun bind(request: TicketRequest): TicketResponse {
        val ticketId = uuid()
        val encryptedPassword = encryptProvider.aesEncrypt(request.uid, request.password)!!
        val ticket = UnicTicket(ticketId, request.uid, encryptedPassword, status = UnicTicketStatus.Initialized)
        val result = unicTicketMapper.addUnicTicket(ticket)
        return if (result > 0) {
            TicketResponse {
                success = true
                this.ticket = ticket.toGrpcTicket()
            }
        } else {
            TicketResponse {
                success = false
                this.ticket = null
            }
        }
    }

    @DbDirect
    fun updateTicketStatus(ticketId: String, status: UnicTicketStatus) {
        unicTicketMapper.updateStatusByTicketId(ticketId, status.value)
    }

}