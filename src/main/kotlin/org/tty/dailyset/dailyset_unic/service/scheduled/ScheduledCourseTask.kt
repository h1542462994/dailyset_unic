package org.tty.dailyset.dailyset_unic.service.scheduled

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.service.PreferenceService
import org.tty.dailyset.dailyset_unic.service.async.CourseFetchCollector
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Component
class ScheduledCourseTask {

    @Autowired
    private lateinit var preferenceService: PreferenceService

    @Autowired
    private lateinit var courseFetchCollector: CourseFetchCollector

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    fun scheduledFetchTask() {
        val rateHour = preferenceService.unicCourseScheduleTaskRateHour
        val lastUpdateTime = preferenceService.unicCourseScheduleLastUpdateTime
        val now = LocalDateTime.now()
        // run every rateHour hours.
        if (Duration.between(lastUpdateTime, now).toHours() >= rateHour) {
            // if duration hour is more than rateHour, then start update
            preferenceService.unicCourseScheduleLastUpdateTime = now
            // then start update.
            courseFetchCollector.pushScheduleTask()
        }
    }
}