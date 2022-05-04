/**
 * create at 2022/4/23
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.service.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import org.tty.dailyset.dailyset_unic.grpc.*
import org.tty.dailyset.dailyset_unic.service.TicketService

@GrpcService
class TicketGrpcService: TicketServiceCoroutineGrpc.TicketServiceImplBase() {

    @Autowired
    private lateinit var ticketService: TicketService

    override suspend fun bind(request: TicketBindRequest): TicketBindResponse {
        return ticketService.bind(request)
    }

    override suspend fun query(request: TicketQueryRequest): TicketQueryResponse {
        return super.query(request)
    }


}