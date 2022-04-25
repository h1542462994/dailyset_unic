package org.tty.dailyset.dailyset_unic.bean.enums

enum class PreferenceName(
    val value: String,
    val defaultValue: String
) {
    UNIC_CURRENT_COURSE_YEAR("unic_current_course_year", "2021"),
    UNIC_CURRENT_COURSE_PERIOD_CODE("unic_current_course_period_code", PeriodCode.SecondTerm.code.toString()),
    UNIC_COURSE_FETCH_RETRY_TIMES("unic_course_fetch_retry_times", "3")

    ;
}