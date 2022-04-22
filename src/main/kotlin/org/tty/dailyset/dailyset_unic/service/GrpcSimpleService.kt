package org.tty.dailyset.dailyset_unic.service

import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.tty.dailyset.dailyset_unic.grpc.HelloReply
import org.tty.dailyset.dailyset_unic.grpc.HelloRequest
import org.tty.dailyset.dailyset_unic.grpc.SimpleGrpc

@GrpcService
class GrpcSimpleService:  SimpleGrpc.SimpleImplBase(){
    override fun sayHello(request: HelloRequest?, responseObserver: StreamObserver<HelloReply>?) {
        val helloReplyBuilder = HelloReply.newBuilder()
        helloReplyBuilder.message = "Hello ${request?.name}"
        val helloReply = helloReplyBuilder.build()

        responseObserver?.onNext(helloReply)
        responseObserver?.onCompleted()
    }
}