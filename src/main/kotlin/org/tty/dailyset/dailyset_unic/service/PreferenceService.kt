/**
 * create at 2022/4/25
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.tty.dailyset.dailyset_unic.bean.annotation.DbDirect
import org.tty.dailyset.dailyset_unic.bean.converters.stringToLocalDateTime
import org.tty.dailyset.dailyset_unic.bean.converters.toStandardString
import org.tty.dailyset.dailyset_unic.bean.enums.PeriodCode
import org.tty.dailyset.dailyset_unic.bean.enums.PreferenceName
import org.tty.dailyset.dailyset_unic.bean.interact.YearPeriod
import org.tty.dailyset.dailyset_unic.mapper.PreferenceMapper
import org.tty.dailyset.dailyset_unic.util.InitSaver
import org.tty.dailyset.dailyset_unic.util.epochLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * service to get and save the preference values.
 */
@Service
class PreferenceService {

    @Autowired
    private lateinit var preferenceMapper: PreferenceMapper


    /**
     * get the stored preference value or get the [PreferenceName.defaultValue] if not found.
     */
    @DbDirect
    private fun getValueOrDefault(preferenceName: PreferenceName): String {
        val default = preferenceName.defaultValue
        val preference = preferenceMapper.findPreference(preferenceName.value)
        val useDefault = preference == null || preference.useDefault
        return checkNotNull(if (useDefault) default else preference?.value)
    }

    /**
     * save the preference value.
     */
    @DbDirect
    private fun setValue(preferenceName: PreferenceName, value: String): Boolean {
        val preference = preferenceMapper.findPreference(preferenceName.value)
        val result: Int = if (preference == null) {
            preferenceMapper.addPreference(preferenceName.value, false, value)
        } else {
            preferenceMapper.setPreference(preferenceName.value, value)
        }
        return result > 0
    }

    /**
     * current course year. like 2022
     */
    @DbDirect
    var unicCurrentCourseYear: Int by InitSaver(0, onInit = {
        getValueOrDefault(PreferenceName.UNIC_CURRENT_COURSE_YEAR).toInt()
    }, onSave = {
        setValue(PreferenceName.UNIC_CURRENT_COURSE_YEAR, it.toString())
    })

    /**
     * current course semester. like 1
     * @see [PeriodCode]
     */
    @DbDirect
    var unicCurrentCoursePeriodCode: PeriodCode by InitSaver(PeriodCode.FirstTerm, onInit = {
        PeriodCode.from(getValueOrDefault(PreferenceName.UNIC_CURRENT_COURSE_PERIOD_CODE).toInt())
    }, onSave = {
        setValue(PreferenceName.UNIC_CURRENT_COURSE_PERIOD_CODE, it.code.toString())
    })

    /**
     * course fetch retry times. like 3
     */
    @DbDirect
    var unicCourseFetchRetryTimes: Int by InitSaver(0, onInit = {
        getValueOrDefault(PreferenceName.UNIC_COURSE_FETCH_RETRY_TIMES).toInt()
    }, onSave = {
        setValue(PreferenceName.UNIC_COURSE_FETCH_RETRY_TIMES, it.toString())
    })

    @DbDirect
    var unicCurrentPeriodAutoConfig: Boolean by InitSaver(false, onInit = {
        getValueOrDefault(PreferenceName.UNIC_CURRENT_PERIOD_AUTO_CONFIG).toBoolean()
    }, onSave = {
        setValue(PreferenceName.UNIC_CURRENT_PERIOD_AUTO_CONFIG, it.toString())
    })

    @DbDirect
    var unicCourseScheduleTaskRateHour: Int by InitSaver(0, onInit = {
        getValueOrDefault(PreferenceName.UNIC_COURSE_SCHEDULE_TASK_RATE_HOUR).toInt()
    }, onSave = {
        setValue(PreferenceName.UNIC_COURSE_SCHEDULE_TASK_RATE_HOUR, it.toString())
    })

    @DbDirect
    var unicCourseScheduleLastUpdateTime: LocalDateTime by InitSaver(epochLocalDateTime(), onInit = {
        stringToLocalDateTime(getValueOrDefault(PreferenceName.UNIC_COURSE_SCHEDULE_LAST_UPDATE_TIME))
    }, onSave = {
        setValue(PreferenceName.UNIC_COURSE_SCHEDULE_LAST_UPDATE_TIME, it.toStandardString())
    })

    /**
     * **unic_course_schedule_task_parallel_size**,课程表任务并行数量,默认为4
     */
    @DbDirect
    var unicCourseScheduleTaskParallelSize: Int by InitSaver(0, onInit = {
        getValueOrDefault(PreferenceName.UNIC_COURSE_SCHEDULE_TASK_PARALLEL_SIZE).toInt()
    }, onSave = {
        setValue(PreferenceName.UNIC_COURSE_SCHEDULE_TASK_PARALLEL_SIZE, it.toString())
    })

    /**
     * the real config year period of course.
     */
    val realYearPeriodNow: YearPeriod
        get() {
            if (!unicCurrentPeriodAutoConfig) {
                // if not auto config, then use the unicCurrentCourseYear and unicCurrentCoursePeriodCode
                return YearPeriod(unicCurrentCourseYear, unicCurrentCoursePeriodCode)
            } else {
                // if auto config, then use the config timeDuration
                val nowDate = LocalDate.now()
//                val timeDuration = unicTimeDurationMapper.findUnicTimeDurationByBetweenStartDateAndEndDate(nowDate)
//                if (timeDuration != null) {
//                    return YearPeriod(timeDuration.year, PeriodCode.from(timeDuration.periodCode))
//                }

                // if not found, use default algorithm
                val year = nowDate.year
                val periodCode = if (nowDate.monthValue in 7..12) {
                    PeriodCode.FirstTerm
                } else {
                    PeriodCode.SecondTerm
                }
                return YearPeriod(year, periodCode)
            }
        }

    val unicCourseCurrentVersion: Int get() {
        synchronized(this) {
            val oldValue = getValueOrDefault(PreferenceName.UNIC_COURSE_CURRENT_VERSION).toInt()
            val newValue = oldValue + 1
            setValue(PreferenceName.UNIC_COURSE_CURRENT_VERSION, newValue.toString())
            return oldValue
        }
    }


}


