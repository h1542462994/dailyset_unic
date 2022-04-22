package org.tty.dailyset.dailyset_unic.grpc

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException

@Deprecated("current use grpc-starter")
@Component
class GrpcLauncher {
    private val logger: Logger = LoggerFactory.getLogger(GrpcLauncher::class.java)

    private var server: Server? = null

    @Value("\${grpc.server.port}")
    private var grpcServerPort = 8088

    @Value("\${grpc.server.security.enabled}")
    private var grpcSecurityEnabled = false

    @Value("\${grpc.server.security.certificate-chain}")
    private lateinit var grpcSecurityCrt: File

    @Value("\${grpc.server.security.private-key}")
    private lateinit var grpcSecurityKey: File

    @Autowired
    private lateinit var context: ConfigurableApplicationContext

//    private fun sslContextBuilder(): SslContextBuilder {
//        val sslContextBuilder = SslContextBuilder.forServer(File(grpcSecurityCA), File(grpcSecurityKey))
//        return GrpcSslContexts.configure(sslContextBuilder, SslProvider.OPENSSL)
//    }

    fun grpcStart(serviceBeanMap: Map<String, Any>) {
        try {
            val serverBuilder = ServerBuilder.forPort(grpcServerPort)
            for (bean in serviceBeanMap.values) {
                serverBuilder.addService(bean as BindableService)
                logger.info("grpc service added: ${bean.javaClass.name}")
            }
            if (grpcSecurityEnabled) {
                serverBuilder.useTransportSecurity(grpcSecurityCrt, grpcSecurityKey)
                logger.info("grpc security enabled")
            }
            server = serverBuilder.build().start()
            logger.info("grpc server started, port: $grpcServerPort")
            server?.awaitTermination()
            Runtime.getRuntime().addShutdownHook(Thread {
                logger.info("grpc server shutdown.")
                grpcStop()
            })
        } catch (e: IOException) {
            logger.error("grpc server start error: ${e.message}")
            e.printStackTrace()
        } catch (e: InterruptedException) {
            logger.error("grpc server start error: ${e.message}")
            e.printStackTrace()
        }

    }

    private fun grpcStop() {
        server?.shutdown()
    }

//    @PostConstruct
    fun init() {
        val beanMap = context.getBeansWithAnnotation(GrpcService::class.java)
        grpcStart(beanMap)
    }
}