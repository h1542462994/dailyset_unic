/**
 * create at 2022/4/21
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.bean.entity

/**
 * entity class -> unic_ticket
 * the ticket for binding account with gdjw.
 */
data class UnicTicket(
    val tickId: String,
    val uid: String,
    val password: String,
    val status: Int
)