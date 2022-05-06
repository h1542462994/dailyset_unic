package org.tty.dailyset.dailyset_unic.intent

import org.tty.dailyset.dailyset_unic.bean.entity.DailySet

data class DailySetUpdateIntent(
    val ticketId: String,
    val dailySet: DailySet
)