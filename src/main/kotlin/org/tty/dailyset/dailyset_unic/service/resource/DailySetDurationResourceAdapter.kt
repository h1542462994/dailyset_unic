package org.tty.dailyset.dailyset_unic.service.resource

import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateItem
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetDuration

class DailySetDurationResourceAdapter: ResourceAdapter<DailySetDuration> {
    override fun getUpdateItems(dailySetUid: String, oldVersion: Int): List<DailySetUpdateItem<DailySetDuration>> {
        TODO("Not yet implemented")
    }
}