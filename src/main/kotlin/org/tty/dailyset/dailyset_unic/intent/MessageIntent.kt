package org.tty.dailyset.dailyset_unic.intent

data class MessageSendIntent(
    val topic: String,
    val referer: String,
    val code: Int,
    val content: String,
)