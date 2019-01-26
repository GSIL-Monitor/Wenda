package com.nowcoder.dao;

import com.nowcoder.model.LoginTicket;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginTicketDao{
    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = "user_id,ticket,expired,status";
    String SELECT_FIELDS = "id,user_id,ticket,expired,status";

    @Insert({"insert into ",TABLE_NAME," (",INSERT_FIELDS,") values (#{userId},#{ticket},#{expired},#{status})"})
    int addTicket(LoginTicket loginTicket);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update ",TABLE_NAME," set status=#{status} where ticket=#{ticket}"})
    // 我突然明白为什么是用User来更新了：改变密码实际上是用户信息的更新，你首先要验证这个USer的账户是否有效，然后再更新
    void updateStatus(@Param("status") int status ,@Param("ticket") String ticket);
}