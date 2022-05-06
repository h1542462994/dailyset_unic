package org.tty.dailyset.dailyset_unic.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.converters.toStandardString
import org.tty.dailyset.dailyset_unic.bean.entity.*
import org.tty.dailyset.dailyset_unic.bean.interact.YearPeriod
import org.tty.dailyset.dailyset_unic.bean.CourseUpdateResult
import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateItemCollection
import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateResult
import org.tty.dailyset.dailyset_unic.bean.enums.*
import org.tty.dailyset.dailyset_unic.intent.DailySetInfosIntent
import org.tty.dailyset.dailyset_unic.intent.DailySetUpdateIntent
import org.tty.dailyset.dailyset_unic.mapper.*
import org.tty.dailyset.dailyset_unic.service.resource.DailySetCourseResourceAdapter
import org.tty.dailyset.dailyset_unic.service.resource.DailySetDurationResourceAdapter
import org.tty.dailyset.dailyset_unic.service.resource.DailySetSchoolIntoMetaResourceAdapter
import org.tty.dailyset.dailyset_unic.service.resource.DailySetStudentInfoMetaResourceAdapter
import org.tty.dailyset.dailyset_unic.util.Diff
import org.tty.dailyset.dailyset_unic.util.addNotNull
import org.tty.dailyset.dailyset_unic.util.uuid
import java.time.LocalDateTime

@Component
class DailySetService {

    @Autowired
    private lateinit var dailySetMapper: DailySetMapper

    @Autowired
    private lateinit var dailySetStudentInfoMetaMapper: DailySetStudentInfoMetaMapper

    @Autowired
    private lateinit var ticketMapper: TicketMapper

    @Autowired
    private lateinit var dailySetMetaLinksMapper: DailySetMetaLinksMapper

    @Autowired
    private lateinit var dailySetCourseMapper: DailySetCourseMapper

    @Autowired
    private lateinit var dailySetSourceLinksMapper: DailySetSourceLinksMapper

    @Autowired
    private lateinit var dailySetCourseResourceAdapter: DailySetCourseResourceAdapter

    @Autowired
    private lateinit var dailySetDurationResourceAdapter: DailySetDurationResourceAdapter

    @Autowired
    private lateinit var dailySetSchoolIntoMetaResourceAdapter: DailySetSchoolIntoMetaResourceAdapter

    @Autowired
    private lateinit var dailySetStudentInfoMetaResourceAdapter: DailySetStudentInfoMetaResourceAdapter

    private val logger = LoggerFactory.getLogger(DailySetService::class.java)

    fun updateOrInsertDailySetStudentInfoMeta(dailySetStudentInfoMeta: DailySetStudentInfoMeta) {
        val dailySetStudentInfoMetaExisted =
            dailySetStudentInfoMetaMapper.findDailySetStudentInfoMetaByMetaUid(dailySetStudentInfoMeta.metaUid)

        if (dailySetStudentInfoMetaExisted != null) {
            dailySetStudentInfoMetaMapper.updateDailySetStudentInfoMeta(dailySetStudentInfoMeta)
        } else {
            dailySetStudentInfoMetaMapper.addDailySetStudentInfoMeta(dailySetStudentInfoMeta)
        }

        val schoolKey = "zjut"
        val setUid = "#school.${schoolKey}.course.${dailySetStudentInfoMeta.metaUid}"
        val dailySet = dailySetMapper.findDailySetByUid(setUid)

        if (dailySet != null) {
            val newMetaVersion = dailySet.metaVersion + 1
            val newDailySet = dailySet.copy(
                metaVersion = newMetaVersion,
            )
            dailySetMapper.updateDailySet(newDailySet)
            val oldStudentInfoLinks = dailySetMetaLinksMapper.findAllDailySetMetaLinksByDailySetUidAndMetaType(
                dailySet.uid,
                DailySetMetaType.StudentInfoMeta.value
            ).single()
            val newStudentInfoLinks = oldStudentInfoLinks.copy(
                insertVersion = newMetaVersion,
            )
            dailySetMetaLinksMapper.updateDailySetMetaLinks(newStudentInfoLinks)
        }
    }

    /**
     * 保证创建了#school.zjut.course.xxx的表
     */
    fun ensureStudentDailySetCreated(uid: String) {
        val schoolKey = "zjut"
        val setUid = "#school.${schoolKey}.course.${uid}"
        val existed = dailySetMapper.findDailySetByUid(setUid)
        if (existed != null) {
            return
        }

        val dailySet = DailySet(uid = setUid, type = DailySetType.ClazzAuto.value, 1, 1, 1)

        dailySetMapper.addDailySet(dailySet)

//        val schoolUid = "#school.${schoolKey}"
//        val schoolInfoMeta = dailySetSchoolInfoMetaMapper.findDailySetSchoolIntoMetaByMetaUid(schoolUid)
//        requireNotNull(schoolInfoMeta) { "schoolInfoMeta is null" }
//
//        dailySetMetaLinksMapper.addDailySetMetaLinks(
//            DailySetMetaLinks(
//                dailySetUid = setUid,
//                metaType = DailySetMetaType.SchoolMeta.value,
//                metaUid = schoolUid,
//                insertVersion = 1,
//                updateVersion = 1,
//                removeVersion = 0,
//                lastTick = LocalDateTime.now()
//            )
//        )

        val studentUid = "#school.${schoolKey}.${uid}"
        dailySetMetaLinksMapper.addDailySetMetaLinks(
            DailySetMetaLinks(
                dailySetUid = setUid,
                metaType = DailySetMetaType.StudentInfoMeta.value,
                metaUid = studentUid,
                insertVersion = 1,
                updateVersion = 1,
                removeVersion = 0,
                lastTick = LocalDateTime.now()
            )
        )
    }

    /**
     * 更新课表数据
     * **complex**
     */
    fun updateWithSource(
        uid: String,
        courses: Iterable<DailySetCourse>,
        yearPeriod: YearPeriod,
        currentVersion: Int
    ): CourseUpdateResult {
        val setUid = "#school.zjut.course.${uid}"
        val existed = dailySetCourseMapper.findAllDailySetCourseByStudentUidAndYearPeriod(
            setUid,
            yearPeriod.year,
            yearPeriod.periodCode.code
        )
        if (existed.isEmpty()) {
            // 添加新的课表?
            val added = courses.map {
                it.copy(sourceUid = uuid())
            }
            withAdded(uid, added, currentVersion)
            logger.info(
                "[${
                    LocalDateTime.now().toStandardString()
                }](${uid},${yearPeriod.year},${yearPeriod.periodCode.code})+${added.size}"
            )
            logger.info(
                "[${
                    LocalDateTime.now().toStandardString()
                }](${uid},${yearPeriod.year},${yearPeriod.periodCode.code})+${added.size}"
            )
            return CourseUpdateResult(added.size, 0)
        } else {
            // 更新已有的课表
            val diff = Diff<DailySetCourse, DailySetCourse, String> {
                source = existed
                target = courses.toList()
                sourceKeySelector = { it.digest }
                targetKeySelector = { it.digest }
            }

            // with additions
            val added = diff.adds.map { it.copy(sourceUid = uuid()) }
//            if (added.isNotEmpty()) {
//                dailySetCourseMapper.addDailySetCoursesBatch(added)
//                val binds = added.map {
//                    DailySetSourceLinks(
//                        dailySetUid = setUid,
//                        sourceType = DailySetSourceType.Course.value,
//                        sourceUid = it.sourceUid,
//                        insertVersion = currentVersion,
//                        updateVersion = 0,
//                        removeVersion = 0,
//                        lastTick = LocalDateTime.now()
//                    )
//                }
//                dailySetSourceLinksMapper.addDailySetSourceLinksBatch(binds)
//            }

            withAdded(uid, added, currentVersion)
            withRemoved(uid, diff.removes, currentVersion)
            logger.info(
                "[${
                    LocalDateTime.now().toStandardString()
                }](${uid},${yearPeriod.year},${yearPeriod.periodCode.code})+${added.size}-${diff.removes.size}~${diff.sames.size}"
            )
            return CourseUpdateResult(added.size, diff.removes.size)
        }
    }

    fun updateSourceVersion(
        uid: String,
        currentVersion: Int
    ) {
        val setUid = "#school.zjut.course.${uid}"
        val dailySet = dailySetMapper.findDailySetByUid(setUid)
        if (dailySet != null) {
            dailySetMapper.updateDailySet(dailySet.copy(sourceVersion = currentVersion))
        }
    }

    private fun withAdded(uid: String, courses: List<DailySetCourse>, currentVersion: Int) {
        if (courses.isEmpty()) {
            return
        }
        val existed = dailySetCourseMapper.findAllDailySetCourseByDigestBatch(
            courses.map { it.digest }
        )
        val diff = Diff<DailySetCourse, DailySetCourse, String> {
            source = existed
            target = courses
            sourceKeySelector = { it.digest }
            targetKeySelector = { it.digest }
        }

        if (diff.adds.isNotEmpty()) {
            dailySetCourseMapper.addDailySetCoursesBatch(diff.adds)
            val binds = diff.adds.map {
                DailySetSourceLinks(
                    dailySetUid = "#school.zjut.course.${uid}",
                    sourceType = DailySetSourceType.Course.value,
                    sourceUid = it.sourceUid,
                    insertVersion = currentVersion,
                    updateVersion = 0,
                    removeVersion = 0,
                    lastTick = LocalDateTime.now()
                )
            }
            dailySetSourceLinksMapper.addDailySetSourceLinksBatch(binds)
        }

        if (diff.sames.isNotEmpty()) {
            val binds = diff.sames.map {
                DailySetSourceLinks(
                    dailySetUid = "#school.zjut.course.${uid}",
                    sourceType = DailySetSourceType.Course.value,
                    sourceUid = it.source.sourceUid,
                    insertVersion = currentVersion,
                    updateVersion = 0,
                    removeVersion = 0,
                    lastTick = LocalDateTime.now()
                )
            }
            dailySetSourceLinksMapper.addDailySetSourceLinksBatch(binds)
        }
    }

    private fun withRemoved(uid: String, courses: List<DailySetCourse>, currentVersion: Int) {
        if (courses.isEmpty()) {
            return
        }

        // read old links.
        val oldLinks = dailySetSourceLinksMapper.findAllDailySetSourceLinksByDailySetUidAndSourceTypeAndSourceUidBatch(
            dailySetUid = "#school.zjut.course.${uid}",
            sourceType = DailySetSourceType.Course.value,
            sourceUids = courses.map { it.sourceUid }
        )

        val newLinks = oldLinks.map {
            it.copy(
                removeVersion = currentVersion,
                lastTick = LocalDateTime.now()
            )
        }

        // save new links
        dailySetSourceLinksMapper.updateDailySetSourceLinksBatch(newLinks)
    }

    suspend fun getDailySetInfos(dailySetInfosIntent: DailySetInfosIntent): List<DailySet>? {
        val ticket = ticketMapper.findUnicTicketByTicketId(dailySetInfosIntent.ticketId) ?: return null
        if (ticket.status !in listOf(UnicTicketStatus.Checked.value, UnicTicketStatus.UnknownFailure.value)) {
            return null
        }

        val unicDailySetUid = "#school.zjut.unic"
        val dailySet1 = dailySetMapper.findDailySetByUid(unicDailySetUid)
        val courseDailySetUid = "#school.zjut.course.${ticket.uid}"
        val dailySet2 = dailySetMapper.findDailySetByUid(courseDailySetUid)

        return listOfNotNull(
            dailySet1, dailySet2
        )
    }

    suspend fun getUpdates(intent: DailySetUpdateIntent): DailySetUpdateResult? {
        val dailySet = dailySetMapper.findDailySetByUid(intent.dailySet.uid) ?: return null
        return getUpdatesOfLocal(intent)
    }

    private suspend fun getUpdatesOfLocal(intent: DailySetUpdateIntent): DailySetUpdateResult? {
        val dailySet = dailySetMapper.findDailySetByUid(intent.dailySet.uid) ?: return null

        val updateItems = mutableListOf<DailySetUpdateItemCollection<*>>()

        //region meta data update
        updateItems.addNotNull(withDailySetSchoolInfoMetaUpdates(intent))
        updateItems.addNotNull(withDailySetStudentInfoMetaUpdates(intent))
        //endregion

        //region source data update
        updateItems.addNotNull(withDailySetDurationUpdates(intent))
        updateItems.addNotNull(withDailySetCourseUpdates(intent))
        //endregion

        return DailySetUpdateResult(
            dailySet,
            updateItems = updateItems
        )

    }

    private suspend fun withDailySetCourseUpdates(intent: DailySetUpdateIntent): DailySetUpdateItemCollection<DailySetCourse>? {
        val dailySetCourseUpdateItems = DailySetUpdateItemCollection(
            type = DailySetDataType.Source.value,
            subType = DailySetSourceType.Course.value,
            updates = dailySetCourseResourceAdapter.getUpdateItems(intent.dailySet.uid, intent.dailySet.sourceVersion)
        )
        return if (dailySetCourseUpdateItems.updates.isNotEmpty()) {
            dailySetCourseUpdateItems
        } else {
            null
        }
    }

    private suspend fun withDailySetDurationUpdates(intent: DailySetUpdateIntent): DailySetUpdateItemCollection<DailySetDuration>? {
        val dailySetDurationUpdateItems = DailySetUpdateItemCollection(
            type = DailySetDataType.Source.value,
            subType = DailySetSourceType.Duration.value,
            updates = dailySetDurationResourceAdapter.getUpdateItems(intent.dailySet.uid, intent.dailySet.sourceVersion)
        )
        return if (dailySetDurationUpdateItems.updates.isNotEmpty()) {
            dailySetDurationUpdateItems
        } else {
            null
        }
    }

    private suspend fun withDailySetSchoolInfoMetaUpdates(intent: DailySetUpdateIntent): DailySetUpdateItemCollection<DailySetSchoolInfoMeta>? {
        val dailySetSchoolInfoMetaUpdateItems = DailySetUpdateItemCollection(
            type = DailySetDataType.Meta.value,
            subType = DailySetMetaType.SchoolMeta.value,
            updates = dailySetSchoolIntoMetaResourceAdapter.getUpdateItems(intent.dailySet.uid, intent.dailySet.metaVersion)
        )
        return if (dailySetSchoolInfoMetaUpdateItems.updates.isNotEmpty()) {
            dailySetSchoolInfoMetaUpdateItems
        } else {
            null
        }
    }

    private suspend fun withDailySetStudentInfoMetaUpdates(intent: DailySetUpdateIntent): DailySetUpdateItemCollection<DailySetStudentInfoMeta>? {
        val dailySetStudentInfoMetaUpdateItems = DailySetUpdateItemCollection(
            type = DailySetDataType.Meta.value,
            subType = DailySetMetaType.StudentInfoMeta.value,
            updates = dailySetStudentInfoMetaResourceAdapter.getUpdateItems(intent.dailySet.uid, intent.dailySet.metaVersion)
        )
        return if (dailySetStudentInfoMetaUpdateItems.updates.isNotEmpty()) {
            dailySetStudentInfoMetaUpdateItems
        } else {
            null
        }
    }

}