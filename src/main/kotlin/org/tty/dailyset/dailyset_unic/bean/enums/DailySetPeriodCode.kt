package org.tty.dailyset.dailyset_unic.bean.enums

enum class DailySetPeriodCode(val value: Int) {
    UnSpecified(0),
    FirstTerm(1),
    FirstTermEnd(2),
    WinterVacation(4),
    SecondTerm(7),
    SecondTermEnd(8),
    ShortTerm(13),
    SummerVacation(14)
}