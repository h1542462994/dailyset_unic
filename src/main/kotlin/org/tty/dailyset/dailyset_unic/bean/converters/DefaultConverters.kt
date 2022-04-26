package org.tty.dailyset.dailyset_unic.bean.converters

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val standardDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun LocalDateTime.toStandardString(): String {
    return this.format(standardDateFormatter)
}

fun stringToLocalDateTime(str: String): LocalDateTime {
    return LocalDateTime.parse(str, standardDateFormatter)
}
