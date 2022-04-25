package org.tty.dailyset.dailyset_unic.bean.interact

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
    val weeksText: String,
    /**
     * **week_day**, like (1)
     */
    val weekDay: String,
    /**
     * **week_day_text**, like (星期一)
     */
    val weekDayText: String,
    /**
     * **time_text**, like (星期一 1-2节)
     */
    val timeText: String,
    /**
     * **weeks_arr**, like (1,2,3..)
     */
    val weeksArr: List<Int>,
    /**
     * **section**, like (1-2)
     */
    val section: String,
)