package org.tty.dailyset.dailyset_unic.bean
import kotlinx.serialization.Serializable

@Serializable
data class MessageIntent(
    val topic: String,
    val referer: String,
    val code: Int,
    val content: String,
)