package com.nowcoder.dao;

import java.util.List;

import com.nowcoder.model.Message;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.Mapping;

@Mapper
public interface MessageDao{
    String TABLE_NAME = "message";
    String INSERT_FIELDS = "from_id,to_id,content,created_date,has_read,conversation_id";
    String SELECT_FIELDS = "id,from_id,to_id,content,created_date,has_read,conversation_id";

    @Insert({"insert into ",TABLE_NAME," (",INSERT_FIELDS,") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    // 查看个人对个人的消息
    @Select({"select ",INSERT_FIELDS," from ",TABLE_NAME," where conversation_id=#{conversationId} order by created_date limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,@Param("offset") int offset,@Param("limit") int limit);

    // 个人消息中心:错误原因：INSERT_FIELDS后面还有个逗号以分隔列名，我给漏了
    @Select({"select ",INSERT_FIELDS," ,count(id) as id from ","(select ",SELECT_FIELDS," from ",TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by created_date desc)"," tt group by conversation_id order by created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);

    //在个人消息中心选出每组对话未读数量,上面的选择语句实际上已经选出了每组的conversationId
    @Select({"select count(id) from ",TABLE_NAME," where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId,@Param("conversationId") String conversationId);


}