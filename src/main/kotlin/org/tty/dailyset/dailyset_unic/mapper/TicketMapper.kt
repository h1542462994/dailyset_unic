package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.tty.dailyset.dailyset_unic.bean.entity.UnicTicket
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus

@Mapper
interface TicketMapper {
    @Insert("insert into ticket(ticket_id, uid, password, status) values(#{ticketId}, #{uid}, #{password}, #{status})")
    fun addUnicTicket(unicTicket: UnicTicket): Int
    @Update("update ticket set status = #{status} where ticket_id = #{ticketId}")
    fun updateStatusByTicketId(ticketId: String, status: Int): Int

    @Select("select * from ticket where ticket_id = #{ticketId}")
    fun findUnicTicketByTicketId(ticketId: String): UnicTicket?

    /**
     * get available tickets, status see [UnicTicketStatus]
     */
    @Select("select * from ticket where status in (0, 1, 2)")
    fun findAllUnicTicketByAvailableStatus(): List<UnicTicket>

    @Select("select * from ticket where uid = #{uid} and status in (1, 2)")
    fun findAllUnicTicketByUidAndOkStatus(uid: String): List<UnicTicket>

    @Update("""
        <script>
            update ticket set status = #{status} where ticket_id in 
            <foreach collection="ticketIds" item="ticketId" open="(" separator="," close=")">#{ticketId}</foreach>
        </script>
    """)
    fun updateStatusBatchByTicketIds(ticketIds: List<String>, status: Int): Int
}