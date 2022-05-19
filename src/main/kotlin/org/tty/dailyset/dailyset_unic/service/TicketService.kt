package org.tty.dailyset.dailyset_unic.service

import org.slf4j.LoggerFactory
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
import org.tty.dailyset.dailyset_unic.grpc.*
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.TicketBindResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.TicketForceFetchResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.TicketQueryResponse
import org.tty.dailyset.dailyset_unic.grpc.TicketProtoBuilders.TicketUnbindResponse
import org.tty.dailyset.dailyset_unic.mapper.DailySetStudentInfoMetaMapper
import org.tty.dailyset.dailyset_unic.mapper.TicketMapper
import org.tty.dailyset.dailyset_unic.service.async.CourseFetchCollector
import org.tty.dailyset.dailyset_unic.util.uuid

@Component
class TicketService: TicketServiceCoroutineGrpc.TicketServiceImplBase() {

    @Autowired
    private lateinit var encryptProvider: EncryptProvider

    @Autowired
    private lateinit var unicTicketMapper: TicketMapper

    @Autowired
    @Lazy
    private lateinit var courseFetchCollector: CourseFetchCollector

    @Autowired
    private lateinit var grpcBeanFactory: GrpcBeanFactory

    @Autowired
    private lateinit var dailySetStudentInfoMetaMapper: DailySetStudentInfoMetaMapper

    private val logger = LoggerFactory.getLogger(TicketService::class.java)

    override suspend fun bind(request: TicketBindRequest): TicketBindResponse {
        logger.debug("have a bind request: ${request.uid}")
        val ticketId = uuid()
        val encryptedPassword = encryptProvider.aesEncrypt(request.uid, request.password)!!
        val ticket = UnicTicket(ticketId, request.uid, encryptedPassword, status = UnicTicketStatus.Initialized)
        val result = unicTicketMapper.addUnicTicket(ticket)
        try {
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
        } catch (e: Exception) {
            logger.error("服务发生了未知异常", e)
            return TicketBindResponse {
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

        logger.debug("have a query request: ${ticketExisted.uid}")
        val studentUid = "#school.zjut.${ticketExisted.uid}"
        val studentInfo = dailySetStudentInfoMetaMapper.findDailySetStudentInfoMetaByMetaUid(studentUid)
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
            // 这里仅通过验证的ticket才会返回信息
            this.studentInfo = if (ticketExisted.status == UnicTicketStatus.Checked.value) {
                studentInfo.toGrpcStudentInfo()
            } else {
                grpcBeanFactory.emptyStudentInfo()
            }
        }
    }

    override suspend fun unbind(request: TicketUnbindRequest): TicketUnbindResponse {
        val ticketId = request.ticketId
        val ticketExisted = unicTicketMapper.findUnicTicketByTicketId(ticketId)
            ?: return TicketUnbindResponse {
                success = false
            }

        logger.debug("have a unbind request: ${ticketExisted.uid}")
        unicTicketMapper.removeUnicTicketByTicketId(ticketId)
        return TicketUnbindResponse {
            success = true
        }
    }

    override suspend fun forceFetch(request: TicketForceFetchRequest): TicketForceFetchResponse {
        val ticketId = request.ticketId
        val ticketExisted = unicTicketMapper.findUnicTicketByTicketId(ticketId)
            ?: return TicketForceFetchResponse {
                success = false
            }

        pushTaskOfNewTicket(ticketExisted)
        return TicketForceFetchResponse {
            success = true
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
    fun findAllUnicTicketByAvailableStatus(): List<UnicTicket> {
        return unicTicketMapper.findAllUnicTicketByAvailableStatus()
    }

    fun findAllUnicTicketByUidAndOkStatus(uid: String): List<UnicTicket> {
        return unicTicketMapper.findAllUnicTicketByUidAndOkStatus(uid)
    }

    fun updateTicketStatusBatch(unicTickets: List<UnicTicket>): Int {
        return unicTickets.groupBy { it.status }.map {
            unicTicketMapper.updateStatusBatchByTicketIds(it.value.map { unicTicket -> unicTicket.ticketId }, it.key)
        }.sum()
    }

}