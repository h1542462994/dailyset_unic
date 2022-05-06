package org.tty.dailyset.dailyset_unic.service.resource

import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateItem

interface ResourceAdapter<T: Any> {
    fun getUpdateItems(dailySetUid: String, oldVersion: Int): List<DailySetUpdateItem<T>>
}