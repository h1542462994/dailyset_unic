package org.tty.dailyset.dailyset_unic.bean.entity

import java.time.LocalTime

class UnicDailyCell(
    val uid: String,
    val currentIndex: Int,
    val start: LocalTime,
    val end: LocalTime,
    val normalType: Int,
    val serialIndex: Int
)