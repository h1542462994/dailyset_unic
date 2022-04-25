package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.InsertProvider
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourseSimple
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourses
import java.text.MessageFormat
import java.util.*

@Mapper
interface UnicCoursesMapper {
    @Select("select * from unic_courses where year = #{year} and period_code = #{periodCode}")
    fun findUnicCourseSimplesByYearAndPeriodCode(year: Int, periodCode: Int): List<UnicCourseSimple>

    @Insert("""
        <script>
            insert into unic_courses(
                course_id,
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
            ) values <foreach collection='list' item='course' index='index' separator=','>
                (
                    #{course.courseId},
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
    fun addUnicCoursesBatch(@Param(value = "list") unicCourses: List<UnicCourses>): Int

}