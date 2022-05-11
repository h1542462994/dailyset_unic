package org.tty.dailyset.dailyset_unic.component

import kotlinx.coroutines.channels.Channel
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.MessageIntent

@Component
class MessageChannel {
    val channel = Channel<MessageIntent> {  }
}