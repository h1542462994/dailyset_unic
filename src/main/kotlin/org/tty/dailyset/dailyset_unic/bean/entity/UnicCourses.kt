package org.tty.dailyset.dailyset_unic.bean.entity

class UnicCourses(
    val courseId: String,
    val year: Int,
    val periodCode: Int,
    val name: String,
    val campus: String,
    val location: String,
    val teacher: String,
    val weeks: String,
    val weekDay: String,
    val sectionStart: Int,
    val sectionEnd: Int,
    val digest: String
)