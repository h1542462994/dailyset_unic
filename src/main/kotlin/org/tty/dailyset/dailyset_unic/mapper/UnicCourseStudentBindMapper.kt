package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourseStudentBind

@Mapper
interface UnicCourseStudentBindMapper {

    @Insert("""
        <script>
            insert into unic_course_student_bind(
                course_id,
                uid
            ) values <foreach collection='list' item='bind' separator=','>
                (#{bind.courseId}, #{bind.uid})
            </foreach>
        </script>
    """)
    fun addUnicCourseStudentBindBatch(list: List<UnicCourseStudentBind>): Int

    @Select("select * from unic_course_student_bind where uid = #{uid}")
    fun findUnicCourseStudentBindByUid(uid: String): List<UnicCourseStudentBind>

    @Delete("""
        delete from unic_course_student_bind where uid = #{uid} and course_id in (
            <foreach collection='list' item='courseId' separator=','>
                #{courseId}
            </foreach>
        )
    """)
    fun removeUnicCourseStudentBindBatchByUid(list: List<String>, uid: String): Int
}