package org.tty.dailyset.dailyset_unic.bean.interact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * python courses, include base info and list of [PythonCourseCell]
 */
@Serializable
data class PythonCourses(
    /**
     *  **class_name** the name of the class (major tutorial class?)
     */

    @SerialName("class_name")
    val className: String,
    /**
     *  **username** username
     */
    val username: String,

    /**
     *  **course_list** list of [PythonCourseCell]
     */
    @SerialName("course_list")
    val courseList: List<PythonCourseCell>
)