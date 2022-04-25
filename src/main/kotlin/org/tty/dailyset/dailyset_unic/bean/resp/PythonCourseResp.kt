package org.tty.dailyset.dailyset_unic.bean.resp

import org.tty.dailyset.dailyset_unic.bean.interact.PythonCourseCollection
import org.tty.dailyset.dailyset_unic.bean.interact.PythonUserInfo

class PythonCourseResp(
    val courses: List<PythonCourseCollection>,
    val userInfo: PythonUserInfo
)