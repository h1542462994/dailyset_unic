package org.tty.dailyset.dailyset_unic.bean

import kotlinx.serialization.Serializable
import org.tty.dailyset.dailyset_unic.bean.ResponseCodes.fail
import org.tty.dailyset.dailyset_unic.bean.ResponseCodes.success

@Serializable
data class Responses<T>(
    val code: Int,
    val message: String,
    val data: T? = null
) {
    companion object {
        /**
         * default fail response entity.
         */
        fun <T> fail(code: Int = ResponseCodes.fail, message: String = "内部错误", data: T? = null): Responses<T>
                = Responses(code, message, data)

        /**
         * default success response entity.
         */
        fun <T> ok(code: Int = ResponseCodes.success, message: String = "请求成功", data: T? = null): Responses<T>
                = Responses(code, message, data)
    }
}