/**
 * create at 2022/4/25
 * @author h1542462994
 */

package org.tty.dailyset.dailyset_unic.bean.entity

/**
 * entity class -> preference
 */
data class Preference(
    val preferenceName: String,
    val useDefault: Boolean,
    val value: String
)
