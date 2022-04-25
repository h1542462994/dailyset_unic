package org.tty.dailyset.dailyset_unic.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.tty.dailyset.dailyset_unic.bean.annotation.DbDirect
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourseSimple
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourses
import org.tty.dailyset.dailyset_unic.bean.entity.UnicStudentInfo
import org.tty.dailyset.dailyset_unic.bean.interact.YearPeriod
import org.tty.dailyset.dailyset_unic.bean.resp.CourseUpdateResp
import org.tty.dailyset.dailyset_unic.mapper.UnicCoursesMapper
import org.tty.dailyset.dailyset_unic.mapper.UnicStudentInfoMapper
import org.tty.dailyset.dailyset_unic.service.async.CourseFetchCollector
import org.tty.dailyset.dailyset_unic.util.uuid
import org.springframework.context.annotation.Lazy
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourseStudentBind
import org.tty.dailyset.dailyset_unic.mapper.UnicCourseStudentBindMapper

@Service
class UnicStudentAndCourseService {
    @Autowired
    private lateinit var unicStudentInfoMapper: UnicStudentInfoMapper

    @Autowired
    private lateinit var unicCoursesMapper: UnicCoursesMapper

    @Autowired
    private lateinit var unicCourseStudentBindMapper: UnicCourseStudentBindMapper


    @DbDirect
    fun updateUnicStudentInfo(unicStudentInfo: UnicStudentInfo): Int {
        val existed = unicStudentInfoMapper.findUnicStudentInfoByUid(unicStudentInfo.uid)
        return if (existed == null) {
            unicStudentInfoMapper.addUnicStudentInfo(unicStudentInfo)
        } else {
            unicStudentInfoMapper.updateUnicStudentInfoByUid(unicStudentInfo)
        }
    }

    @DbDirect
    fun getExistedCoursesSampleInfo(uid: String): List<UnicCourseSimple> {
        // TODO: 2020/7/31 可以考虑使用缓存
        TODO()
    }

    fun updateWithSource(uid: String, courses: Iterable<UnicCourses>, yearPeriod: YearPeriod): CourseUpdateResp {
        val courseSimples = unicCoursesMapper.findUnicCourseSimplesByYearAndPeriodCode(yearPeriod.year, yearPeriod.periodCode.code)
        return if (courseSimples.isEmpty()) {
            val added = courses.map { it.copy(courseId = uuid()) }
            if (added.isNotEmpty()) {
                unicCoursesMapper.addUnicCoursesBatch(added)
                val binds = added.map { UnicCourseStudentBind(it.courseId, uid) }
                unicCourseStudentBindMapper.addUnicCourseStudentBindBatch(binds)
            }
            CourseUpdateResp(true, listOf())
        } else {
            // TODO: 添加更新逻辑
            CourseUpdateResp(false, listOf())
        }
    }
}