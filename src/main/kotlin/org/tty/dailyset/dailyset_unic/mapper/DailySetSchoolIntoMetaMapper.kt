package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetSchoolInfoMeta

@Mapper
interface DailySetSchoolIntoMetaMapper {

    @Select("select * from dailyset_school_info_meta where uid = #{uid}")
    fun findDailySetSchoolIntoMeta(uid: String): DailySetSchoolInfoMeta?

    fun addDailySetSchoolIntoMeta(dailySetStudentInfoMeta: DailySetSchoolInfoMeta)
}