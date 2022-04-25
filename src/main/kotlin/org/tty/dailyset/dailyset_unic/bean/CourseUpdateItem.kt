package org.tty.dailyset.dailyset_unic.bean

import org.tty.dailyset.dailyset_unic.bean.enums.UpdateCode

class CourseUpdateItem(
    val updateCode: UpdateCode,
    val oldId: String,
    val newId: String
)