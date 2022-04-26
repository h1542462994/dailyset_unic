/**
 * create at 2022/4/16
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.util

import okhttp3.internal.and
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun File.child(name: String): File = File(this, name)

/**
 * slow functions
 */
fun byte2Hex(bytes: ByteArray): String {
    val stringBuilder = StringBuilder()
    var temp: String
    for (byte in bytes) {
        temp = Integer.toHexString(byte and 0xFF)
        if (temp.length == 1) {
            stringBuilder.append("0")
        }
        stringBuilder.append(temp)
    }
    return stringBuilder.toString()
}

fun hex2Byte(hex: String): ByteArray {
    val bytes = ByteArray(hex.length / 2)
    for (i in hex.indices step 2) {
        bytes[i / 2] = Integer.parseInt(hex.substring(i, i + 2), 16).toByte()
    }
    return bytes
}

fun anyTextEmpty(vararg texts: String?): Boolean {
    return texts.any { it.isNullOrEmpty() }
}

fun anyIntEmpty(vararg ints: Int?): Boolean {
    return ints.any { it == null }
}

fun uuid(): String {
    return UUID.randomUUID().toString()
}

fun getToken(auth: String): String?{
    return if (auth.startsWith("Bearer ")) {
        auth.substring("Bearer ".length)
    } else {
        null
    }
}


fun md5(text: String): String {
    val messageDigest: MessageDigest
    try {
        messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(text.toByteArray(StandardCharsets.UTF_8))
        return byte2Hex(messageDigest.digest())
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

fun nowDisplay(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}