package org.tty.dailyset.dailyset_unic.component

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.netty.GrpcSslContexts
import io.grpc.netty.NettyChannelBuilder
import io.netty.handler.ssl.SslContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

@Component
class TestBeanFactory {
    @Value("\${grpc.server.port}")
    private var grpcPort: Int = 0
    @Value("\${grpc.server.address.test}")
    private lateinit var grpcAddress: String
    @Value("\${grpc.server.security.enabled}")
    private var grpcSecurityEnabled = false
    @Value("\${test.grpc.client.certification-authority}")
    private lateinit var testGrpcClientCA: File
    @Value("\${test.grpc.client.certificate-chain}")
    private lateinit var testGrpcClientCrt: File
    @Value("\${test.grpc.client.private-key}")
    private lateinit var testGrpcKey: File

    private var channel: ManagedChannel? = null
    private var sslContext: SslContext? = null

    fun getSslContext(): SslContext {
        if (sslContext == null) {
            val builder = GrpcSslContexts.forClient()
            builder.trustManager(testGrpcClientCA)
            builder.keyManager(testGrpcClientCrt, testGrpcKey)
            sslContext = builder.build()
        }
        return sslContext!!
    }

    fun getChannel(): ManagedChannel {
        if (channel == null) {
            channel = if (!grpcSecurityEnabled) {
                ManagedChannelBuilder.forAddress(grpcAddress, grpcPort)
                    .usePlaintext()
                    .build()
            } else {
                NettyChannelBuilder.forAddress(grpcAddress, grpcPort)
                    .sslContext(getSslContext())
                    .build()
            }

        }
        return channel!!
    }
}