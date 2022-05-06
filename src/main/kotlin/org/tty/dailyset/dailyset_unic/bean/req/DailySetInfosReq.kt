package org.tty.dailyset.dailyset_unic.bean.req

import org.tty.dailyset.dailyset_unic.util.anyTextEmpty

class DailySetInfosReq(
    val ticketId: String?,
    // why request should contain more than 1 argument?
    val abc: Int?
) {
    fun verify(): Boolean {
        return !anyTextEmpty(ticketId)
    }
}