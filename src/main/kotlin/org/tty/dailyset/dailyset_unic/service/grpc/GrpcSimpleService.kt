package org.tty.dailyset.dailyset_unic.service.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.tty.dailyset.dailyset_unic.grpc.HelloReply
import org.tty.dailyset.dailyset_unic.grpc.HelloRequest
import org.tty.dailyset.dailyset_unic.grpc.SimpleCoroutineGrpc
import org.tty.dailyset.dailyset_unic.grpc.HelloWorldProtoBuilders.HelloReply

@GrpcService
class GrpcSimpleService: SimpleCoroutineGrpc.SimpleImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        return HelloReply {
            message = "Hello ${request.name}"
        }
    }
}