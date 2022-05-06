package org.tty.dailyset.dailyset_unic.service.resource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateItem
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetStudentInfoMeta
import org.tty.dailyset.dailyset_unic.bean.enums.DailySetMetaType
import org.tty.dailyset.dailyset_unic.mapper.DailySetMetaLinksMapper
import org.tty.dailyset.dailyset_unic.mapper.DailySetStudentInfoMetaMapper
import java.time.LocalDateTime

@Component
class DailySetStudentInfoMetaResourceAdapter: ResourceAdapter<DailySetStudentInfoMeta> {

    @Autowired
    private lateinit var dailySetMetaLinksMapper: DailySetMetaLinksMapper

    @Autowired
    private lateinit var dailySetStudentInfoMetaMapper: DailySetStudentInfoMetaMapper

    override fun getUpdateItems(
        dailySetUid: String,
        oldVersion: Int
    ): List<DailySetUpdateItem<DailySetStudentInfoMeta>> {
        val dailySetMetaLinks = dailySetMetaLinksMapper.findAllDailySetMetaLinkByDailySetUidAndSourceTypeAndVersionsLargerThan(
            dailySetUid,
            DailySetMetaType.StudentInfoMeta.value,
            oldVersion
        )
        if (dailySetMetaLinks.isEmpty()) {
            return emptyList()
        }

        val dailySetMetaLink = dailySetMetaLinks[0]
        val dailySetStudentInfoMeta = dailySetStudentInfoMetaMapper.findDailySetStudentInfoMetaByMetaUid(dailySetMetaLink.metaUid)
        return listOf(
            DailySetUpdateItem(
                insertVersion = dailySetMetaLink.insertVersion,
                updateVersion = dailySetMetaLink.updateVersion,
                removeVersion = dailySetMetaLink.removeVersion,
                lastTick = dailySetMetaLink.lastTick,
                data = dailySetStudentInfoMeta
            )
        )
    }
}