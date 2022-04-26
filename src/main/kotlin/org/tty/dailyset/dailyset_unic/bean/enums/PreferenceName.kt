package org.tty.dailyset.dailyset_unic.bean.enums

enum class PreferenceName(
    val value: String,
    val defaultValue: String
) {
    /**
     * **unic_current_course_year**,当前的学年
     */
    UNIC_CURRENT_COURSE_YEAR("unic_current_course_year", "2021"),

    /**
     * **unic_current_course_period_code**,当前的时期
     */
    UNIC_CURRENT_COURSE_PERIOD_CODE("unic_current_course_period_code", PeriodCode.SecondTerm.code.toString()),

    /**
     * **unic_current_period_auto_config**,是否自动配置当前的学期和时期,默认为false,需要手动配置
     */
    UNIC_CURRENT_PERIOD_AUTO_CONFIG("unic_current_period_auto_config", "false"),

    /**
     * **unic_course_fetch_retry_times**,进行数据获取次数的最大重试次数,一般为3
     */
    UNIC_COURSE_FETCH_RETRY_TIMES("unic_course_fetch_retry_times", "0"),

    /**
     * **unic_course_schedule_task_rate_hour**,课程表更新频率(单位:小时),默认为24小时
     */
    UNIC_COURSE_SCHEDULE_TASK_RATE_HOUR("unic_course_schedule_task_rate_hour", "24"),

    /**
     * **unic_course_schedule_last_update_time**,课程表最后更新时间
     */
    UNIC_COURSE_SCHEDULE_LAST_UPDATE_TIME("unic_course_schedule_last_update_time", "1970-01-01 00:00:00"),
    ;
}