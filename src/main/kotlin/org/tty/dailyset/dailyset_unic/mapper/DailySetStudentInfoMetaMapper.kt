package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetStudentInfoMeta

@Mapper
interface DailySetStudentInfoMetaMapper {
    @Select("select * from dailyset_student_info_meta where meta_uid = #{metaUid}")
    fun findDailySetStudentInfoMetaByMetaUid(metaUid: String): DailySetStudentInfoMeta?

    @Insert("""
        insert into dailyset_student_info_meta (meta_uid, department_name, class_name, name, grade)
        values (#{metaUid}, #{departmentName}, #{className}, #{name}, #{grade})
    """)
    fun addDailySetStudentInfoMeta(dailySetStudentInfoMeta: DailySetStudentInfoMeta): Int

    @Update("""
        update dailyset_student_info_meta set department_name = #{departmentName}, class_name = #{className}, name = #{name}, grade = #{grade}
        where meta_uid = #{metaUid}
    """)
    fun updateDailySetStudentInfoMeta(dailySetStudentInfoMeta: DailySetStudentInfoMeta): Int

}