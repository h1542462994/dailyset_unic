package org.tty.dailyset.dailyset_unic.bean.interact

import org.tty.dailyset.dailyset_unic.bean.ResponseCodes

object PythonResponseCode {
    val PythonResponseCode.success: Int get() = 0
    val PythonResponseCode.loginFail: Int get() = 1
    val PythonResponseCode.unknown: Int get() = 2
}