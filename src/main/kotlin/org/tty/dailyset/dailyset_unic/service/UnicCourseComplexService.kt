package org.tty.dailyset.dailyset_unic.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.tty.dailyset.dailyset_unic.bean.CourseUpdateItem
import org.tty.dailyset.dailyset_unic.bean.annotation.DbDirect
import org.tty.dailyset.dailyset_unic.bean.converters.toStandardString
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourseSimple
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourseStudentBind
import org.tty.dailyset.dailyset_unic.bean.entity.UnicCourses
import org.tty.dailyset.dailyset_unic.bean.entity.UnicStudentInfo
import org.tty.dailyset.dailyset_unic.bean.enums.UpdateCode
import org.tty.dailyset.dailyset_unic.bean.interact.YearPeriod
import org.tty.dailyset.dailyset_unic.bean.resp.CourseUpdateResp
import org.tty.dailyset.dailyset_unic.mapper.UnicCourseStudentBindMapper
import org.tty.dailyset.dailyset_unic.mapper.UnicCoursesMapper
import org.tty.dailyset.dailyset_unic.mapper.UnicStudentInfoMapper
import org.tty.dailyset.dailyset_unic.util.Diff
import org.tty.dailyset.dailyset_unic.util.uuid
import java.time.LocalDateTime

@Deprecated("use DailySetService instead.")
@Service
class UnicCourseComplexService {
    @Autowired
    private lateinit var unicStudentInfoMapper: UnicStudentInfoMapper

    @Autowired
    private lateinit var unicCoursesMapper: UnicCoursesMapper

    @Autowired
    private lateinit var unicCourseStudentBindMapper: UnicCourseStudentBindMapper

    private val logger = LoggerFactory.getLogger(UnicCourseComplexService::class.java)


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
    fun findExistCourseInfosByUidAndYearPeriod(uid: String, yearPeriod: YearPeriod): List<UnicCourseSimple> {
        return unicCoursesMapper.findUnicCourseSimplesByUidAndYearAndPeriodCode(uid, yearPeriod.year, yearPeriod.periodCode.code)
    }



    fun updateWithSource(uid: String, courses: Iterable<UnicCourses>, yearPeriod: YearPeriod): CourseUpdateResp {
        val existed = findExistCourseInfosByUidAndYearPeriod(uid, yearPeriod)
        return if (existed.isEmpty()) {
            // updateItems
            val updateItems = mutableListOf<CourseUpdateItem>()
            // if no existed course, insert all data, and create binding.
            val added = courses.map { it.copy(courseId = uuid()).apply {
                updateItems.add(CourseUpdateItem(UpdateCode.Added, "", courseId))
            } }
            if (added.isNotEmpty()) {
                unicCoursesMapper.addUnicCoursesBatch(added)
                val binds = added.map { UnicCourseStudentBind(it.courseId, uid) }
                unicCourseStudentBindMapper.addUnicCourseStudentBindBatch(binds)
            }
            logger.info("[${LocalDateTime.now().toStandardString()}](${uid},${yearPeriod.year},${yearPeriod.periodCode.code})+${added.size}")
            CourseUpdateResp(true, updateItems)
        } else {
            // calculate diff
            val diff = Diff<UnicCourseSimple, UnicCourses, String> {
                source = existed
                target = courses.toList()
                sourceKeySelector = { it.digest }
                targetKeySelector = { it.digest }
            }

            // updateItems
            val updateItems = mutableListOf<CourseUpdateItem>()

            // with additions ?default?
            val added = diff.adds.map { it.copy(courseId = uuid()) }
            if (added.isNotEmpty()) {
                unicCoursesMapper.addUnicCoursesBatch(added)
                val binds = added.map { UnicCourseStudentBind(it.courseId, uid).apply {
                    updateItems.add(CourseUpdateItem(UpdateCode.Added, "", courseId))
                } }
                unicCourseStudentBindMapper.addUnicCourseStudentBindBatch(binds)
            }
            val removed = diff.removes
            if (removed.isNotEmpty()) {
                unicCourseStudentBindMapper.removeUnicCourseStudentBindBatchByUid(removed.map { it.apply {
                    updateItems.add(CourseUpdateItem(UpdateCode.Removed, courseId, ""))
                }.courseId }, uid)
            }

            logger.info("[${LocalDateTime.now().toStandardString()}](${uid},${yearPeriod.year},${yearPeriod.periodCode.code})+${added.size}-${removed.size}~${diff.sames.size}")
            CourseUpdateResp(false, updateItems)
        }
    }
}