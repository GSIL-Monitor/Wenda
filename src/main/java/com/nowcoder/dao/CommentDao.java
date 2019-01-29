package com.nowcoder.dao;

import java.util.List;

import com.nowcoder.model.Question;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommentDao{
    String TABLE_NAME = "comment ";
    String INSERT_FIELDS = "content,user_id,entity_id,entity_type,created_date,status";
    String SELECT_FIELDS = "id,content,user_id,entity_id,entity_type,created_date,status";

    @Insert({"insert into ",TABLE_NAME," (",INSERT_FIELDS,") values (#{content},#{userId},#{entityId},#{entityType},#{createdDate},#{status})"})
    int addComment(Comment comment);

    @Update({"update ",TABLE_NAME," set status=#{status} where id=#{id}"})
    int updateCommentStatusById( @Param("status") int status,@Param("id") int id);

    @Delete({"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(int id);


    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}"})
    List<Comment> getComments(@Param("entityId") int entityId,@Param("entityType") int entityType);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    Comment getCommentById(int id);

    @Select({"select count(id) as id from",TABLE_NAME," where  entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentsCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);
 }