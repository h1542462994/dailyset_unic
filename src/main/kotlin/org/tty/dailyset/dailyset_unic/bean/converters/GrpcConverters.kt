package org.tty.dailyset.dailyset_unic.bean.converters

import org.tty.dailyset.dailyset_unic.bean.entity.UnicStudentInfo
import org.tty.dailyset.dailyset_unic.bean.entity.Ticket
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus.*
import org.tty.dailyset.dailyset_unic.grpc.MessageProtoBuilders.MessageResponse
import org.tty.dailyset.dailyset_unic.grpc.MessageResponse
import org.tty.dailyset.dailyset_unic.grpc.StudentInfo
import org.tty.dailyset.dailyset_unic.grpc.Ticket
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.StudentInfo
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.Ticket
import org.tty.dailyset.dailyset_unic.intent.MessageSendIntent

fun org.tty.dailyset.dailyset_unic.bean.entity.Ticket.toGrpcTicket(): Ticket {
    return Ticket {
        ticketId = this@toGrpcTicket.ticketId
        uid = this@toGrpcTicket.uid
        status = UnicTicketStatus.of(this@toGrpcTicket.status).toGrpcTicketStatus()
    }
}

fun UnicTicketStatus.toGrpcTicketStatus(): Ticket.TicketStatus {
    return when (this) {
        Initialized -> Ticket.TicketStatus.Initialized
        Checked -> Ticket.TicketStatus.Checked
        UnknownFailure -> Ticket.TicketStatus.Failure
        LoginFailure -> Ticket.TicketStatus.PasswordFailure
    }
}

fun UnicStudentInfo.toGrpcStudentInfo(): StudentInfo {
    return StudentInfo {
        this.uid = this@toGrpcStudentInfo.uid
        this.departmentName = this@toGrpcStudentInfo.departmentName
        this.className = this@toGrpcStudentInfo.className
        this.name = this@toGrpcStudentInfo.name
        this.grade = this@toGrpcStudentInfo.grade
    }
}

fun MessageSendIntent.toGrpcMessage(): MessageResponse {
    return MessageResponse {
        topic = this@toGrpcMessage.topic
        referer = this@toGrpcMessage.referer
        code = this@toGrpcMessage.code
        content = this@toGrpcMessage.content
    }
}