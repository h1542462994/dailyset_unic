package org.tty.dailyset.dailyset_unic.bean

import java.time.LocalDateTime

interface UpdatableItemLink {
    val insertVersion: Int
    val updateVersion: Int
    val removeVersion: Int
    val lastTick: LocalDateTime
}