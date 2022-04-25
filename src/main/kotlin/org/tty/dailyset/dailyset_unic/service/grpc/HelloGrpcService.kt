package org.tty.dailyset.dailyset_unic.service.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.tty.dailyset.dailyset_unic.grpc.HelloReply
import org.tty.dailyset.dailyset_unic.grpc.HelloRequest
import org.tty.dailyset.dailyset_unic.grpc.HelloCoroutineGrpc
import org.tty.dailyset.dailyset_unic.grpc.HelloProtoBuilders.HelloReply

@GrpcService
class HelloGrpcService: HelloCoroutineGrpc.HelloImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        return HelloReply {
            message = "Hello ${request.name}"
        }
    }
}