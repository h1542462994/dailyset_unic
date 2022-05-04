package org.tty.dailyset.dailyset_unic.component

import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.grpc.StudentInfo
import org.tty.dailyset.dailyset_unic.grpc.Ticket
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.StudentInfo
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.Ticket

@Component
class GrpcBeanFactory {
    fun emptyTicket(): Ticket {
        return Ticket {
            ticketId = ""
            uid = ""
            status = Ticket.TicketStatus.Initialized
        }
    }

    fun emptyStudentInfo(): StudentInfo {
        return StudentInfo {
            uid = ""
            departmentName = ""
            className = ""
            name = ""
            grade = 0
        }
    }
}