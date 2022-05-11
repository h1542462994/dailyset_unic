package org.tty.dailyset.dailyset_unic.http

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import retrofit2.Retrofit

@Component
class RetrofitHttpStubsFactory {

    @Autowired
    private lateinit var retrofit: Retrofit

    @Bean
    fun dailySetCloudApi(): DailySetCloudApi {
        return retrofit.create(DailySetCloudApi::class.java)
    }
}