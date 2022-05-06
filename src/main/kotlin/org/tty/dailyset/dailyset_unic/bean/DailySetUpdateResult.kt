package org.tty.dailyset.dailyset_unic.bean

import org.tty.dailyset.dailyset_unic.bean.entity.DailySet

data class DailySetUpdateResult(
    val dailySet: DailySet,

    /**
     * updateItems
     */
    val updateItems: List<DailySetUpdateItemCollection<*>>
)