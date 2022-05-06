package org.tty.dailyset.dailyset_unic.component

import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.bean.entity.DailySet
import org.tty.dailyset.dailyset_unic.bean.req.DailySetUpdateReq
import org.tty.dailyset.dailyset_unic.intent.DailySetUpdateIntent

@Component
class IntentFactory {
    fun createDailySetUpdateIntent(dailySetUpdateReq: DailySetUpdateReq): DailySetUpdateIntent {
        return DailySetUpdateIntent(
            ticketId = dailySetUpdateReq.ticketId!!,
            dailySet = DailySet(
                uid = dailySetUpdateReq.uid!!,
                type = dailySetUpdateReq.type!!,
                sourceVersion = dailySetUpdateReq.sourceVersion!!,
                matteVersion = dailySetUpdateReq.matteVersion!!,
                metaVersion = dailySetUpdateReq.metaVersion!!
            )
        )
    }
}