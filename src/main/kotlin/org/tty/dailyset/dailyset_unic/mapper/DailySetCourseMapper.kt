package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetCourse
import org.tty.dailyset.dailyset_unic.bean.interact.YearPeriod

@Mapper
interface DailySetCourseMapper {
    @Select("""
        select * from dailyset_course where source_uid in (
            select source_uid from dailyset_source_links where dailyset_uid = concat('#school.zjut.course', #{studentUid}) and source_type = 10
        ) and year = #{year} and period_code = #{periodCode}
    """)
    fun findAllDailySetCourseByStudentUidAndYearPeriod(studentUid: String, year: Int, periodCode: Int): List<DailySetCourse>

    @Insert("""
        <script>
            insert into dailyset_course (
                source_uid,
                year,
                period_code,
                name,
                campus,
                location,
                teacher,
                weeks,
                week_day,
                section_start,
                section_end,
                digest
           ) values <foreach collection="courses" item="course" separator=",">
                (
                    #{course.sourceUid},
                    #{course.year},
                    #{course.periodCode},
                    #{course.name},
                    #{course.campus},
                    #{course.location},
                    #{course.teacher},
                    #{course.weeks},
                    #{course.weekDay},
                    #{course.sectionStart},
                    #{course.sectionEnd},
                    #{course.digest}
                )
           </foreach>
        </script>
    """)
    fun addDailySetCoursesBatch(courses: List<DailySetCourse>): Int

    @Select("""
        <script>
            select * from dailyset_course where digest in (
                <foreach collection="digests" item="digest" separator=",">
                    #{digest}
                </foreach>
            )
        </script>
    """)
    fun findAllDailySetCourseByDigestBatch(digests: List<String>): List<DailySetCourse>

    @Select("""
        <script>
            select * from dailyset_course where source_uid in 
            <foreach collection="sourceUids" item="sourceUid" open="(" separator="," close=")">
                #{sourceUid}
            </foreach>
        </script>
    """)
    fun findAllDailySetCourseBySourceUidBatch(sourceUids: List<String>): List<DailySetCourse>
}