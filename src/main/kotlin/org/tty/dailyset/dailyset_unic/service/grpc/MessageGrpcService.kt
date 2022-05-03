package org.tty.dailyset.dailyset_unic.service.grpc

import kotlinx.coroutines.channels.SendChannel
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import org.tty.dailyset.dailyset_unic.bean.converters.toGrpcMessage
import org.tty.dailyset.dailyset_unic.component.MessageChannel
import org.tty.dailyset.dailyset_unic.grpc.CreateMessageChannelRequest
import org.tty.dailyset.dailyset_unic.grpc.MessageResponse
import org.tty.dailyset.dailyset_unic.grpc.MessageServiceCoroutineGrpc

@GrpcService
class MessageGrpcService: MessageServiceCoroutineGrpc.MessageServiceImplBase() {
    @Autowired
    private lateinit var messageChannel: MessageChannel

    override suspend fun createMessageChannel(
        request: CreateMessageChannelRequest,
        responseChannel: SendChannel<MessageResponse>
    ) {
        while (true) {
            val message = messageChannel.channel.receive()
            responseChannel.send(message.toGrpcMessage())
        }
    }
}