package com.nowcoder.dao;

import java.util.List;

import com.nowcoder.model.Question;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QuestionDao{
    String TABLE_NAME = "question ";
    String INSERT_FIELDS = "title,content,user_id,created_date,comment_count";
    String SELECT_FIELDS = "id,title,content,user_id,created_date,comment_count";

    @Insert({"insert into ",TABLE_NAME," (",INSERT_FIELDS,") values (#{title},#{content},#{userId},#{createdDate},#{commentCount})"})
    int addQuestion(Question question);

    @Update({"update ",TABLE_NAME," set content=#{content} where id=#{id}"})
    void updateQuestionContentById(Question question);

    @Delete({"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(int id);

    // 关键语句：选出多个问题（最新问题显示）,但是如果没登录呢给user_id赋为0
    List<Question> selectLatestQuestions(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);


    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    Question getQuestionById(int id);
 }