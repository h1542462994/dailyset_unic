package org.tty.dailyset.dailyset_unic.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetStudentInfoMeta
import org.tty.dailyset.dailyset_unic.mapper.DailySetStudentInfoMetaMapper

@Component
class DailySetService {

    @Autowired
    private lateinit var dailySetStudentInfoMetaMapper: DailySetStudentInfoMetaMapper

    fun updateOrInsertDailySetStudentInfoMeta(dailySetStudentInfoMeta: DailySetStudentInfoMeta) {
        val dailySetStudentInfoMetaExisted = dailySetStudentInfoMetaMapper.findDailySetStudentInfoMetaByStudentId(dailySetStudentInfoMeta.uid)

        if (dailySetStudentInfoMetaExisted != null) {
            dailySetStudentInfoMetaMapper.updateDailySetStudentInfoMeta(dailySetStudentInfoMeta)
        } else {
            dailySetStudentInfoMetaMapper.addDailySetStudentInfoMeta(dailySetStudentInfoMeta)
        }
    }
}