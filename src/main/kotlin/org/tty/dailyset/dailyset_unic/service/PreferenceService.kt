/**
 * create at 2022/4/25
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.tty.dailyset.dailyset_unic.bean.annotation.DbDirect
import org.tty.dailyset.dailyset_unic.bean.enums.PreferenceName
import org.tty.dailyset.dailyset_unic.mapper.PreferenceMapper
import org.tty.dailyset.dailyset_unic.util.InitSaver
import org.tty.dailyset.dailyset_unic.bean.enums.PeriodCode

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
    var unicCurrentCourseTerm: Int by InitSaver(0, onInit = {
        getValueOrDefault(PreferenceName.UNIC_CURRENT_COURSE_PERIOD_CODE).toInt()
    }, onSave = {
        setValue(PreferenceName.UNIC_CURRENT_COURSE_PERIOD_CODE, it.toString())
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

    

}


