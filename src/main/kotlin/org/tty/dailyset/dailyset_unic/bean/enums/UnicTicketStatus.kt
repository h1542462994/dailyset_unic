package org.tty.dailyset.dailyset_unic.bean.enums

enum class UnicTicketStatus(val value: Int) {
    Initialized(0),
    Checked(1),
    Failure(2);

    companion object {
        fun of(value: Int): UnicTicketStatus = values().first { it.value == value }
    }
}