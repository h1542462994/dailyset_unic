package org.tty.dailyset.dailyset_unic.bean.entity

import java.time.LocalDate

@Deprecated("user dailysetDuration instead.")
class UnicTimeDuration(
    val timeDurationId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val year: Int,
    val periodCode: Int
)