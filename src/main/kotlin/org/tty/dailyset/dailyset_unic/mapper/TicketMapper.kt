package org.tty.dailyset.dailyset_unic.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.tty.dailyset.dailyset_unic.bean.entity.Ticket
import org.tty.dailyset.dailyset_unic.bean.enums.UnicTicketStatus

@Mapper
interface TicketMapper {
    @Insert("insert into ticket(ticket_id, uid, password, status) values(#{ticketId}, #{uid}, #{password}, #{status})")
    fun addUnicTicket(unicTicket: Ticket): Int
    @Update("update ticket set status = #{status} where ticket_id = #{ticketId}")
    fun updateStatusByTicketId(ticketId: String, status: Int): Int

    @Select("select * from ticket where ticket_id = #{ticketId}")
    fun findUnicTicketByTicketId(ticketId: String): Ticket?

    /**
     * get available tickets, status see [UnicTicketStatus]
     */
    @Select("select * from ticket where status in (0, 1, 2)")
    fun findUnicTicketsByAvailableStatus(): List<Ticket>

    @Update("""
        <script>
            update ticket set status = #{status} where ticket_id in 
            <foreach collection="ticketIds" item="ticketId" open="(" separator="," close=")">#{ticketId}</foreach>
        </script>
    """)
    fun updateStatusBatchByTicketIds(ticketIds: List<String>, status: Int): Int
}