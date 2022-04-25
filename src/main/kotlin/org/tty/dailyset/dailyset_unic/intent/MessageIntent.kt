package org.tty.dailyset.dailyset_unic.intent

import okio.ByteString

data class MessageSendIntent(
    val topic: String,
    val referer: String,
    val code: Int,
    val message: String,
)