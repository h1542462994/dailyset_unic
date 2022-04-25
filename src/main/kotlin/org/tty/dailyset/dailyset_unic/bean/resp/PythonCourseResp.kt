package org.tty.dailyset.dailyset_unic.bean.resp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tty.dailyset.dailyset_unic.bean.interact.PythonCourseCollection
import org.tty.dailyset.dailyset_unic.bean.interact.PythonUserInfo

@Serializable
class PythonCourseResp(
    val courses: List<PythonCourseCollection>,
    @SerialName("user_info")
    val userInfo: PythonUserInfo
)