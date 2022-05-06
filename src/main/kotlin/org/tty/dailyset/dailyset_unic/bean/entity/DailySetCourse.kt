package org.tty.dailyset.dailyset_unic.bean.entity

import org.tty.dailyset.dailyset_unic.bean.enums.DailySetPeriodCode

data class DailySetCourse(
    val sourceUid: String,
    val year: Int,
    /**
     * @see DailySetPeriodCode
     */
    val periodCode: Int,
    val name: String,
    val campus: String,
    val location: String,
    val teacher: String,
    val weeks: String,
    val weekDay: Int,
    val sectionStart: Int,
    val sectionEnd: Int,
    val digest: String
)