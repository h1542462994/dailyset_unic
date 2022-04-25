package org.tty.dailyset.dailyset_unic.bean.resp

import org.tty.dailyset.dailyset_unic.bean.CourseUpdateItem

class CourseUpdateResp(
    val batchLoad: Boolean = false,
    val data: List<CourseUpdateItem>
)