package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.tty.dailyset.dailyset_unic.bean.entity.Preference

@Mapper
interface PreferenceMapper {
    @Select("select * from preference where preference_name = #{preferenceName}")
    fun findPreference(preferenceName: String): Preference?

    @Insert("insert into preference(preference_name, use_default, value) values (#{preferenceName}, #{useDefault}, #{value})")
    fun addPreference(preferenceName: String, useDefault: Boolean, value: String): Int

    @Update("update preference set value = #{value} where preference_name = #{preferenceName}")
    fun setPreference(preferenceName: String, value: String): Int

    @Update("update preference set use_default = True where preference_name = #{preferenceName}")
    fun resetReference(preferenceName: String): Int
}