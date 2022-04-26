/**
 * create at 2022/4/25
 * @author h15424626994
 */

package org.tty.dailyset.dailyset_unic.bean.enums

enum class UnicTicketStatus(val value: Int) {
    /**
     * initialized *available*
     */
    Initialized(0),

    /**
     * checked *available*
     */
    Checked(1),

    /**
     * failed *available*
     */
    UnknownFailure(2),

    /**
     * password_error *not available*
     */
    LoginFailure(3), ;

    companion object {
        fun of(value: Int): UnicTicketStatus = values().first { it.value == value }
    }
}