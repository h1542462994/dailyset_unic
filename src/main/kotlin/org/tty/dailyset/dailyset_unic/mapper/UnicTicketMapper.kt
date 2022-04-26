package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus

@Mapper
interface UnicTicketMapper {
    @Insert("insert into unic_ticket(ticket_id, uid, password, status) values(#{ticketId}, #{uid}, #{password}, #{status})")
    fun addUnicTicket(unicTicket: UnicTicket): Int
    @Update("update unic_ticket set status = #{status} where ticket_id = #{ticketId}")
    fun updateStatusByTicketId(ticketId: String, status: Int): Int

    /**
     * get available tickets, status see [UnicTicketStatus]
     */
    @Select("select * from unic_ticket where status in (0, 1, 2)")
    fun findUnicTicketsByAvailableStatus(): List<UnicTicket>

    @Update("""
        <script>
            update unic_ticket set status = #{status} where ticket_id in 
            <foreach collection="ticketIds" item="ticketId" open="(" separator="," close=")">#{ticketId}</foreach>
        </script>
    """)
    fun updateStatusBatchByTicketIds(ticketIds: List<String>, status: Int): Int
}