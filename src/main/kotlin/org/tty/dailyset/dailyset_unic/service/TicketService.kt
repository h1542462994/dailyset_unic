package org.tty.dailyset.dailyset_unic.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.ResponseCodes
import org.tty.dailyset.dailyset_unic.bean.annotation.DbDirect
import org.tty.dailyset.dailyset_unic.bean.converters.toGrpcStudentInfo
import org.tty.dailyset.dailyset_unic.bean.converters.toGrpcTicket
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus
import org.tty.dailyset.dailyset_unic.component.EncryptProvider
import org.tty.dailyset.dailyset_unic.component.GrpcBeanFactory
import org.tty.dailyset.dailyset_unic.grpc.TicketBindRequest
import org.tty.dailyset.dailyset_unic.grpc.TicketBindResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.TicketBindResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.TicketQueryResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketQueryRequest
import org.tty.dailyset.dailyset_unic.grpc.TicketQueryResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketServiceCoroutineGrpc
import org.tty.dailyset.dailyset_unic.mapper.UnicStudentInfoMapper
import org.tty.dailyset.dailyset_unic.mapper.UnicTicketMapper
import org.tty.dailyset.dailyset_unic.service.async.CourseFetchCollector
import org.tty.dailyset.dailyset_unic.util.uuid

@Component
class TicketService: TicketServiceCoroutineGrpc.TicketServiceImplBase() {

    @Autowired
    private lateinit var encryptProvider: EncryptProvider

    @Autowired
    private lateinit var unicTicketMapper: UnicTicketMapper

    @Autowired
    @Lazy
    private lateinit var courseFetchCollector: CourseFetchCollector

    @Autowired
    private lateinit var grpcBeanFactory: GrpcBeanFactory

    @Autowired
    private lateinit var unicStudentInfoMapper: UnicStudentInfoMapper

    override suspend fun bind(request: TicketBindRequest): TicketBindResponse {
        val ticketId = uuid()
        val encryptedPassword = encryptProvider.aesEncrypt(request.uid, request.password)!!
        val ticket = UnicTicket(ticketId, request.uid, encryptedPassword, status = UnicTicketStatus.Initialized)
        val result = unicTicketMapper.addUnicTicket(ticket)
        return if (result > 0) {
            pushTaskOfNewTicket(ticket)
            TicketBindResponse {
                success = true
                this.ticket = ticket.toGrpcTicket()
            }
        } else {
            TicketBindResponse {
                success = false
                this.ticket = grpcBeanFactory.emptyTicket()
            }
        }
    }

    override suspend fun query(request: TicketQueryRequest): TicketQueryResponse {
        val ticketId = request.ticketId
        val ticketExisted = unicTicketMapper.findUnicTicketByTicketId(ticketId)
            ?: return TicketQueryResponse {
                code = ResponseCodes.ticketNotExist
                message = "ticket不存在"
                ticket = grpcBeanFactory.emptyTicket()
                studentInfo = grpcBeanFactory.emptyStudentInfo()
            }

        val studentInfo = unicStudentInfoMapper.findUnicStudentInfoByUid(ticketExisted.uid)
            ?: return TicketQueryResponse {
                code = ResponseCodes.success
                message = "学生信息不存在"
                ticket = ticketExisted.toGrpcTicket()
                studentInfo = grpcBeanFactory.emptyStudentInfo()
            }

        return TicketQueryResponse {
            code = ResponseCodes.success
            message = "查询成功"
            ticket = ticketExisted.toGrpcTicket()
            this.studentInfo = studentInfo.toGrpcStudentInfo()
        }
    }

    private fun pushTaskOfNewTicket(ticket: UnicTicket) {
        courseFetchCollector.pushTaskOfNewTicket(ticket)
    }

    @DbDirect
    fun updateTicketStatus(ticketId: String, status: UnicTicketStatus): Int {
        return unicTicketMapper.updateStatusByTicketId(ticketId, status.value)
    }

    @DbDirect
    fun findUnicTicketsByAvailableStatus(): List<UnicTicket> {
        return unicTicketMapper.findUnicTicketsByAvailableStatus()
    }

    fun updateTicketStatusBatch(unicTickets: List<UnicTicket>): Int {
        return unicTickets.groupBy { it.status }.map {
            unicTicketMapper.updateStatusBatchByTicketIds(it.value.map { unicTicket -> unicTicket.ticketId }, it.key)
        }.sum()
    }

}