/**
 * create at 2022/4/21
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.bean.entity

import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus

/**
 * entity class -> unic_ticket
 * the ticket for binding account with gdjw.
 */
data class UnicTicket(
    val ticketId: String,
    val uid: String,
    val password: String,
    val status: Int
) {
    constructor(ticketId: String, uid: String, password: String, status: UnicTicketStatus) : this(ticketId, uid, password, status.value)
}
