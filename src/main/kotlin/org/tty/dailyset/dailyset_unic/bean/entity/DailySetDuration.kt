package org.tty.dailyset.dailyset_unic.bean.entity

import java.time.LocalDate
import org.tty.dailyset.dailyset_unic.bean.enums.DailySetPeriodCode

data class DailySetDuration(
    val sourceUid: String,
    val type: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val name: String,
    val tag: String,
    val bindingYear: Int,
    /**
     * @see DailySetPeriodCode
     */
    val bindingPeriodCode: Int
)