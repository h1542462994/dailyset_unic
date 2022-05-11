package org.tty.dailyset.dailyset_unic.http

import org.tty.dailyset.dailyset_unic.bean.Responses
import org.tty.dailyset.dailyset_unic.http.req.MessagePostReq
import retrofit2.http.Body
import retrofit2.http.POST

interface DailySetCloudApi {

    @POST("/message/post/ticket")
    suspend fun messagePostTicket(@Body messagePostReq: MessagePostReq): Responses<Int>
}