package org.tty.dailyset.dailyset_unic.service.resource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateItem
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetCourse
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetDuration
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetSourceLinks
import org.tty.dailyset.dailyset_unic.bean.enums.DailySetSourceType
import org.tty.dailyset.dailyset_unic.mapper.DailySetDurationMapper
import org.tty.dailyset.dailyset_unic.mapper.DailySetSourceLinksMapper

@Component
class DailySetDurationResourceAdapter: ResourceAdapter<DailySetDuration> {

    @Autowired
    private lateinit var dailySetSourceLinkMapper: DailySetSourceLinksMapper

    @Autowired
    private lateinit var dailySetDurationMapper: DailySetDurationMapper

    override fun getUpdateItems(dailySetUid: String, oldVersion: Int): List<DailySetUpdateItem<DailySetDuration>> {
        val dailySetSourceLinks =
            dailySetSourceLinkMapper.findAllDailySetSourceLinksByDailySetUidAndSourceTypeAndVersionLargerThan(
                dailySetUid,
                DailySetSourceType.Duration.value,
                oldVersion
            )
        if (dailySetSourceLinks.isEmpty()) {
            return emptyList()
        }

        val dailySetDurations = dailySetDurationMapper.findAllDailySetDurationBySourceUidBatch(
            dailySetSourceLinks.map { it.sourceUid }
        )
        return join2Sources(dailySetSourceLinks, dailySetDurations)
    }

    private fun join2Sources(
        dailySetSourceLinks: List<DailySetSourceLinks>,
        dailySetDurations: List<DailySetDuration>
    ): List<DailySetUpdateItem<DailySetDuration>> {
        val dailySetDurationMap = hashMapOf(*dailySetDurations.map { it.sourceUid to it }.toTypedArray())

        return dailySetSourceLinks.map {
            DailySetUpdateItem(
                insertVersion = it.insertVersion,
                updateVersion = it.updateVersion,
                removeVersion = it.removeVersion,
                lastTick = it.lastTick,
                data = dailySetDurationMap[it.sourceUid]
            )
        }
    }
}