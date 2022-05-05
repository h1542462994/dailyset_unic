package org.tty.dailyset.dailyset_unic.bean.entity

import java.time.LocalTime

@Deprecated("function is migrated to <cloud>")
class UnicDailyCell(
    val uid: String,
    val currentIndex: Int,
    val start: LocalTime,
    val end: LocalTime,
    val normalType: Int,
    val serialIndex: Int
)