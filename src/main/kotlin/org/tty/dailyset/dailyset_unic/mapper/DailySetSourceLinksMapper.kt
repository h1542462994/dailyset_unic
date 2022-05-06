package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetSourceLinks

@Mapper
interface DailySetSourceLinksMapper {
    @Insert("""
        <script>
            insert into dailyset_source_links(
                dailyset_uid,
                source_type,
                source_uid,
                insert_version,
                update_version,
                remove_version,
                last_tick
            ) values <foreach collection="links" item="link" separator=",">
                (
                    #{link.dailySetUid},
                    #{link.sourceType},
                    #{link.sourceUid},
                    #{link.insertVersion},
                    #{link.updateVersion},
                    #{link.removeVersion},
                    #{link.lastTick}
                )
            </foreach>
        </script>
    """)
    fun addDailySetSourceLinksBatch(links: List<DailySetSourceLinks>): Int

    @Update("""
        <script>
            <foreach collection = "links" item = "link" separator = ";">
                update dailyset_source_links
                set
                    update_version = #{link.updateVersion},
                    remove_version = #{link.removeVersion},
                    last_tick = #{link.lastTick}
                where
                    dailyset_id = #{link.dailysetId}
                    and source_type = #{link.sourceType}
                    and source_uid = #{link.sourceUid}
            </foreach>
            ;
        </script>
    """)
    fun updateDailySetSourceLinksBatch(links: List<DailySetSourceLinks>): Int


    @Select("""
        <script>
            select * from dailyset_source_links where dailyset_id = #{dailySetUid} and source_type = #{sourceType}
            and source_uid in <foreach collection="sourceUids" item="sourceUid" open="(" separator="," close=")">
                #{sourceUid}
            </foreach>
        </script>
    """)
    fun findAllDailySetSourceLinksByDailySetUidAndSourceTypeAndSourceUidBatch(dailySetUid: String, sourceType: Int, sourceUids: List<String>): List<DailySetSourceLinks>
}