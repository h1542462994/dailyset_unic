package org.tty.dailyset.dailyset_unic.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.tty.dailyset.dailyset_unic.bean.DailySetUpdateResult
import org.tty.dailyset.dailyset_unic.bean.ResponseCodes
import org.tty.dailyset.dailyset_unic.bean.Responses
import org.tty.dailyset.dailyset_unic.bean.req.DailySetUpdateReq
import org.tty.dailyset.dailyset_unic.component.IntentFactory
import org.tty.dailyset.dailyset_unic.service.DailySetService

@RestController
class DailySetController {

    @Autowired
    private lateinit var dailySetService: DailySetService

    @Autowired
    private lateinit var intentFactory: IntentFactory

    @PostMapping("/dailyset/update")
    suspend fun dailySetUpdate(@RequestBody dailySetUpdateReq: DailySetUpdateReq): Responses<DailySetUpdateResult> {
        if (!dailySetUpdateReq.verify()) {
            return Responses.argError()
        }

        val intent = intentFactory.createDailySetUpdateIntent(dailySetUpdateReq)
        val data = dailySetService.getUpdates(intent)
        return if (data == null) {
            Responses.fail(code = ResponseCodes.dailySetNotExist, "日程表数据不存在")
        } else {
            Responses.ok(data = dailySetService.getUpdates(intent))
        }
    }
}