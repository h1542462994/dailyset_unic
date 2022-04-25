package org.tty.dailyset.dailyset_unic.bean.interact

import kotlinx.serialization.Serializable

/**
 * the collection of the python course, includes year, term and list of [PythonCourses]
 */
@Serializable
data class PythonCourseCollection(
    /**
     * **year**, the year of the course recorded.
     */
    val year: Int,
    /**
     * **term**, the term of the course recorded.
     */
    val term: Int,
    /**
     * **courses**
     */
    val courses: PythonCourses
)