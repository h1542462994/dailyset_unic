package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.tty.dailyset.dailyset_unic.bean.entity.DailySetMetaLinks

@Mapper
interface DailySetMetaLinksMapper {

    @Select("select * from dailyset_meta_links where dailyset_id = #{dailySetId} and meta_type = #{metaType}")
    fun findAllDailySetMetaLinksByDailySetUidAndMetaType(dailySetUid: String, metaType: Int): List<DailySetMetaLinks>

    @Insert("""
        insert into dailyset_meta_links (dailyset_uid, meta_type, meta_uid, insert_version, update_version, remove_version, last_tick)
        values (#{dailySetUid}, #{metaType}, #{metaUid}, #{insertVersion}, #{updateVersion}, #{removeVersion}, #{lastTick})
    """)
    fun addDailySetMetaLinks(dailySetMetaLinks: DailySetMetaLinks): Int

    @Update("""
        update dailyset_meta_links set update_version = #{updateVersion}, remove_version = #{removeVersion}, last_tick = #{lastTick}
        where dailyset_uid = #{dailySetUid} and meta_type = #{metaType} and meta_uid = #{metaUid}
    """)
    fun updateDailySetMetaLinks(dailySetMetaLinks: DailySetMetaLinks): Int
}