package org.tty.dailyset.dailyset_unic.bean.interact

/**
 * python courses, include base info and list of [PythonCourseCell]
 */
data class PythonCourses(
    /**
     *  **class_name** the name of the class (major tutorial class?)
     */
    val className: String,
    /**
     *  **username** username
     */
    val username: String,
    /**
     *  **course_list** list of [PythonCourseCell]
     */
    val courseList: List<PythonCourseCell>
)