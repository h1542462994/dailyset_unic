package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetDuration

@Mapper
interface DailySetDurationMapper {
    @Select("""
        <script>
            select * from dailyset_duration where source_uid in 
            <foreach collection="sourceUids" item="sourceUid" open="(" separator="," close=")">
                #{sourceUid}
            </foreach>
        </script>
    """)
    fun findAllDailySetDurationBySourceUidBatch(sourceUids: List<String>): List<DailySetDuration>
}