package org.tty.dailyset.dailyset_unic.service.resource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateItem
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetSchoolInfoMeta
import org.tty.dailyset.dailyset_unic.bean.enums.DailySetMetaType
import org.tty.dailyset.dailyset_unic.mapper.DailySetMetaLinksMapper
import org.tty.dailyset.dailyset_unic.mapper.DailySetSchoolIntoMetaMapper
import java.time.LocalDateTime

@Component
class DailySetSchoolIntoMetaResourceAdapter: ResourceAdapter<DailySetSchoolInfoMeta> {

    @Autowired
    private lateinit var dailySetMetaLinksMapper: DailySetMetaLinksMapper

    @Autowired
    private lateinit var dailySetSchoolIntoMetaMapper: DailySetSchoolIntoMetaMapper

    override fun getUpdateItems(
        dailySetUid: String,
        oldVersion: Int
    ): List<DailySetUpdateItem<DailySetSchoolInfoMeta>> {
        val dailySetMetaLinks = dailySetMetaLinksMapper.findAllDailySetMetaLinkByDailySetUidAndSourceTypeAndVersionsLargerThan(
            dailySetUid,
            DailySetMetaType.SchoolMeta.value,
            oldVersion
        )
        if (dailySetMetaLinks.isEmpty()) {
            return emptyList()
        }

        val dailySetMetaLink = dailySetMetaLinks[0]
        val dailySetSchoolInfoMeta = dailySetSchoolIntoMetaMapper.findDailySetSchoolIntoMetaByMetaUid(
            dailySetMetaLink.metaUid
        )
        return listOf(
            DailySetUpdateItem(
                insertVersion = dailySetMetaLink.insertVersion,
                updateVersion = dailySetMetaLink.updateVersion,
                removeVersion = dailySetMetaLink.removeVersion,
                lastTick = dailySetMetaLink.lastTick,
                data = dailySetSchoolInfoMeta
            )
        )

    }
}