package org.tty.dailyset.dailyset_unic.http

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import java.net.InetAddress
import java.util.concurrent.TimeUnit

@Component
class RetrofitFactory {
    @Value("\${dailyset.env.client.address}")
    private lateinit var cloudAddress: String

    @Value("\${dailyset.env.client.usedocker}")
    private var useDocker: Boolean = false

    @Value("\${dailyset.env.client.http.port}")
    private var httpPort: Int = 8088

    private val logger = LoggerFactory.getLogger(RetrofitFactory::class.java)

    @OptIn(ExperimentalSerializationApi::class)
    @Bean
    fun retrofit(): Retrofit {
        val contentType = "application/json".toMediaType()

        var address = "http://${cloudAddress}:${httpPort}"
        if (useDocker) {
            val inetAddress = InetAddress.getByName(cloudAddress)
            address = "http://${inetAddress.hostAddress}:${httpPort}"
        }

        logger.debug("realCloudAddress is $address")

        return Retrofit.Builder()
            .client(okHttpClient())
            .baseUrl(address)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    private fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
    }
}