package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.tty.dailyset.dailyset_unic.bean.entity.UnicStudentInfo

@Mapper
interface UnicStudentInfoMapper {
    @Select("select * from unic_student_info where uid = #{uid}")
    fun findUnicStudentInfoByUid(uid: String): UnicStudentInfo?

    @Insert("insert into unic_student_info(uid, department_name, class_name, name, grade) values (#{uid}, #{departmentName}, #{className}, #{name}, #{grade})")
    fun addUnicStudentInfo(unicStudentInfo: UnicStudentInfo): Int

    @Update("update unic_student_info set department_name = #{departmentName}, class_name = #{className}, name = #{name}, grade = #{grade} where uid = #{uid}")
    fun updateUnicStudentInfoByUid(unicStudentInfo: UnicStudentInfo): Int
}