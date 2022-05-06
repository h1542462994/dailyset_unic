package org.tty.dailyset.dailyset_unic.service.resource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateItem
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetCourse
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetSourceLinks
import org.tty.dailyset.dailyset_unic.bean.enums.DailySetSourceType
import org.tty.dailyset.dailyset_unic.mapper.DailySetCourseMapper
import org.tty.dailyset.dailyset_unic.mapper.DailySetSourceLinksMapper

@Component
class DailySetCourseResourceAdapter : ResourceAdapter<DailySetCourse> {

    @Autowired
    private lateinit var dailySetSourceLinkMapper: DailySetSourceLinksMapper

    @Autowired
    private lateinit var dailySetCourseMapper: DailySetCourseMapper

    override fun getUpdateItems(dailySetUid: String, oldVersion: Int): List<DailySetUpdateItem<DailySetCourse>> {
        val dailySetSourceLinks =
            dailySetSourceLinkMapper.findAllDailySetSourceLinksByDailySetUidAndSourceTypeAndVersionLargerThan(
                dailySetUid,
                DailySetSourceType.Course.value,
                oldVersion
            )
        if (dailySetSourceLinks.isEmpty()) {
            return emptyList()
        }

        val dailySetCourses = dailySetCourseMapper.findAllDailySetCourseBySourceUidBatch(
            dailySetSourceLinks.map { it.sourceUid }
        )
        return join2Sources(dailySetSourceLinks, dailySetCourses)
    }

    private fun join2Sources(
        dailySetSourceLinks: List<DailySetSourceLinks>,
        dailySetCourses: List<DailySetCourse>
    ): List<DailySetUpdateItem<DailySetCourse>> {
        val dailySetCourseMap = hashMapOf(*dailySetCourses.map { it.sourceUid to it }.toTypedArray())

        return dailySetSourceLinks.map {
            DailySetUpdateItem(
                insertVersion = it.insertVersion,
                updateVersion = it.updateVersion,
                removeVersion = it.removeVersion,
                lastTick = it.lastTick,
                data = dailySetCourseMap[it.sourceUid]
            )
        }
    }
}