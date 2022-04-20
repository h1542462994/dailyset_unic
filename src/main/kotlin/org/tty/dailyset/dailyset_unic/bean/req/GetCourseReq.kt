package org.tty.dailyset.dailyset_unic.bean.req

import org.tty.dailyset.dailyset_unic.util.anyIntEmpty
import org.tty.dailyset.dailyset_unic.util.anyTextEmpty

class GetCourseReq(
    val uid: String? = null,
    val password: String? = null,
    val year: Int? = null,
    val term: Int? = null
) {

    fun verify(): Boolean {
        return !anyIntEmpty(year, term) && !anyTextEmpty(uid, password)
    }
}