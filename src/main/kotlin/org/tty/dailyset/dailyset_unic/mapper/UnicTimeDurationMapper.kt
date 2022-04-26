package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTimeDuration
import java.time.LocalDate

@Mapper
interface UnicTimeDurationMapper {
    @Select("select * from unic_time_duration where year = #{year} and period_code = #{periodCode}")
    fun findUnicTimeDurationByYearAndPeriodCode(year: Int, periodCode: Int): UnicTimeDuration?

    @Select("select * from unic_time_duration where start_date <= #{nowDate} and end_date > #{nowDate}")
    fun findUnicTimeDurationByBetweenStartDateAndEndDate(nowDate: LocalDate): UnicTimeDuration?
}