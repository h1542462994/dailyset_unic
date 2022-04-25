package org.tty.dailyset.dailyset_unic.bean.interact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PythonCourseCell(
    /**
     * **course** course name
     */
    var course: String,
    /**
     * **place** place of the course
     */
    var place: String,
    /**
     * **campus** campus of the course
     */
    var campus: String,
    /**
     * **teacher** teacher of the course
     */
    var teacher: String,
    /**
     * ~~**weeks_text** weeks text of the course, like (1-16周)
     */
    @SerialName("weeks_text")
    val weeksText: String,
    /**
     * **week_day**, like (1)
     */
    @SerialName("week_day")
    val weekDay: String,
    /**
     * **week_day_text**, like (星期一)
     */
    @SerialName("week_day_text")
    val weekDayText: String,
    /**
     * **time_text**, like (星期一 1-2节)
     */
    @SerialName("time_text")
    val timeText: String,
    /**
     * **weeks_arr**, like (1,2,3..)
     */
    @SerialName("weeks_arr")
    val weeksArr: List<Int>,
    /**
     * **section**, like (1-2)
     */
    val section: String,
)