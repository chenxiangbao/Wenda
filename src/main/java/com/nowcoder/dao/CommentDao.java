package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface CommentDao {
    String TABLE_NAME="comment";
    String INSERT_FIELDS=" content,user_id,created_date,entity_id,entity_type,status ";
    String SELECT_FIELDS=" id , " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, "(", INSERT_FIELDS, " ) values (#{content},#{userId},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    @Select({"select ",SELECT_FIELDS , " from ", TABLE_NAME, " where id=#{id}"})
    Comment getByCommentId(int id);

    List<Comment> getCommentByEntityId(@Param("entityId") int entityId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    @Select({"select count(id) from" , TABLE_NAME , " where entity_id=#{entityId} and entity_type = #{entityType}"})
    int getCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

    @Select({"select count(id) from" , TABLE_NAME , " where user_id=#{userId}"})
    int getUserCommentCount(@Param("userId") int userId);

    @Update({"update comment set status=#{status} where id=#{id} "})
    boolean updateComment(@Param("id") int id,@Param("status") int status);
}
