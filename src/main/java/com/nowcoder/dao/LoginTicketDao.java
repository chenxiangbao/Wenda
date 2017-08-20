package com.nowcoder.dao;

import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LoginTicketDao {
    String TABLE_NAME="login_ticket";
    String INSERT_FIELDS=" user_id,ticket,expired,status ";
    String SELECT_FIELDS=" id , " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, "(", INSERT_FIELDS, " ) values (#{userId},#{ticket},#{expired},#{status})"})
    int addLoginTicket(LoginTicket loginTicket);

    @Update({"update ", TABLE_NAME ," set status=1 where id=#{id}"})
    void updateById(int id);

    @Update({"update ", TABLE_NAME ," set status=1 where ticket=#{ticket}"})
    void updateByTicket(String  ticket);

    @Select({"select ",SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    LoginTicket getByTicket(String ticket);
}
