/**
 * create at 2022/4/18
 * @author h1542462994
 *
 * Dailyset unic server, used for get student's classTable and timeTable infos.
 * zjut school, gdjw system.
 */

package org.tty.dailyset.dailyset_unic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class DailysetUnicApplication

fun main(args: Array<String>) {
    runApplication<DailysetUnicApplication>(*args)
}
