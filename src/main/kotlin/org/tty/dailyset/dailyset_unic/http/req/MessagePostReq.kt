

package org.tty.dailyset.dailyset_unic.http.req

import org.tty.dailyset.dailyset_unic.bean.MessageIntent
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class MessagePostReq(
    val secret: String,
    val targets: List<String>,
    val intent: MessageIntent
)