package org.tty.dailyset.dailyset_unic.intent

import org.tty.dailyset.dailyset_unic.bean.MessageIntent

data class MessagePostIntent(
    val targets: List<String>,
    val intent: MessageIntent
)