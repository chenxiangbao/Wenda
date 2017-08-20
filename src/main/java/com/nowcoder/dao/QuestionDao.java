package com.nowcoder.dao;

import com.nowcoder.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDao {
    String TABLE_NAME="question";
    String INSERT_FIELDS=" title,content,user_id,created_date,comment_count ";
    String SELECT_FIELDS=" id , " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, "( " ,INSERT_FIELDS,") values(#{title},#{content},#{userId},#{createdDate},#{commentCount})"})
    int addQuestion(Question question);

    @Select({"select ",SELECT_FIELDS, " from ",TABLE_NAME, " where id=#{id}"})
    Question selectById(int id);

    @Update({"update ", TABLE_NAME ," set content=#{content} where id=#{id}"})
    void updateById(Question question);

    @Update({"update ",TABLE_NAME," set comment_count=#{count} where id=#{entityId}"})
    void updateCount(@Param("entityId") int entityId,@Param("count") int count);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteById( int id);

    List<Question> selectQuestionByUserId(@Param("userId") int userId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    List<Question> selectAllQuestion(@Param("offset") int offset,
                                     @Param("limit") int limit);
}
