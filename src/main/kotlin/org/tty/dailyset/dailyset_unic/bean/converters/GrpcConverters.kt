package org.tty.dailyset.dailyset_unic.bean.converters

import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus
import org.tty.dailyset.dailyset_unic.grpc.Ticket
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.Ticket

fun UnicTicket.toGrpcTicket(): Ticket {
    return Ticket {
        ticketId = this@toGrpcTicket.ticketId
        uid = this@toGrpcTicket.uid
        status = UnicTicketStatus.of(this@toGrpcTicket.status).toGrpcTicketStatus()
    }
}

fun UnicTicketStatus.toGrpcTicketStatus(): Ticket.TicketStatus {
    return when (this) {
        UnicTicketStatus.Initialized -> Ticket.TicketStatus.Initialized
        UnicTicketStatus.Checked -> Ticket.TicketStatus.Checked
        UnicTicketStatus.Failure -> Ticket.TicketStatus.Failure
    }
}