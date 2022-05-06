package org.tty.dailyset.dailyset_unic.bean.converters

import org.tty.dailyset.dailyset_unic.bean.entity.DailySetCourse
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetStudentInfoMeta
import org.tty.dailyset.dailyset_unic.bean.enums.PeriodCode
import org.tty.dailyset.dailyset_unic.bean.interact.PythonCourseCollection
import org.tty.dailyset.dailyset_unic.bean.interact.PythonUserInfo
import org.tty.dailyset.dailyset_unic.bean.interact.YearPeriod
import org.tty.dailyset.dailyset_unic.bean.resp.PythonCourseResp
import org.tty.dailyset.dailyset_unic.util.md5

fun PythonUserInfo.toDailySetStudentInfoMeta(): DailySetStudentInfoMeta {
    return DailySetStudentInfoMeta(
        metaUid = "#school.zjut.${this.studentNumber}" ,
        departmentName = this.departmentName,
        className = this.className,
        name = this.name,
        grade = this.grade.toInt(),
    )
}


fun PythonCourseCollection.toDailySetCourses(): Sequence<DailySetCourse> = sequence {
    for (course in this@toDailySetCourses.courses.courseList) {
        val sectionPair = selectionTextToPair(course.section)
        val str = "${year}${term}${course.course}${course.campus}${course.place}${course.teacher}${course.weeksArr}${course.weekDay}${sectionPair.first}${sectionPair.second}"
        val data = DailySetCourse(
            sourceUid = "",
            year = this@toDailySetCourses.year,
            periodCode = termToPeriodCode(this@toDailySetCourses.term).code,
            name = course.course,
            campus = course.campus,
            location = course.place,
            teacher = course.teacher,
            weeks = course.weeksArr.toString(),
            weekDay = course.weekDay.toInt(),
            sectionStart = sectionPair.first,
            sectionEnd = sectionPair.second,
            digest = md5(str)
        )
        yield(data)
    }
}

fun PythonCourseResp.yearPeriods(): List<YearPeriod> {
    return this.courses.map {
        YearPeriod(
            year = it.year,
            periodCode = termToPeriodCode(it.term)
        )
    }
}

fun PythonCourseResp.select(yearPeriod: YearPeriod): PythonCourseCollection {
    return this.courses.first {
        it.year == yearPeriod.year && termToPeriodCode(it.term) == yearPeriod.periodCode
    }
}

private fun selectionTextToPair(selectionText: String): Pair<Int, Int> {
    val selection = selectionText.split("-")
    return if (selection.size == 1) {
        Pair(selection[0].toInt(), selection[0].toInt())
    } else {
        Pair(selection[0].toInt(), selection[1].toInt())
    }
}

fun termToPeriodCode(term: Int): PeriodCode {
    return when (term) {
        1 -> {
            PeriodCode.FirstTerm
        }
        2 -> {
            PeriodCode.SecondTerm
        }
        else -> {
            PeriodCode.UnSpecified
        }
    }
}