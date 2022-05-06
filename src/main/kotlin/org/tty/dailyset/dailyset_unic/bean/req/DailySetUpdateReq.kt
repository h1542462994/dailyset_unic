package org.tty.dailyset.dailyset_unic.bean.req

import org.tty.dailyset.dailyset_unic.util.anyIntEmpty
import org.tty.dailyset.dailyset_unic.util.anyTextEmpty

class DailySetUpdateReq(
    val ticketId: String?,
    val uid: String?,
    val type: Int?,
    val sourceVersion: Int?,
    val matteVersion: Int?,
    val metaVersion: Int?
) {
    fun verify(): Boolean {
        return !anyIntEmpty(type, sourceVersion, matteVersion, metaVersion) && !anyTextEmpty(ticketId, uid)
    }
}