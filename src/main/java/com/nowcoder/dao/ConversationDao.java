package com.nowcoder.dao;

import com.nowcoder.model.Conversation;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConversationDao {
    String TABLE_NAME="conversation";
    String INSERT_FIELDS=" from_id,to_id,content,created_date,has_read,conversation_id ";
    String SELECT_FIELDS=" id , " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, "(", INSERT_FIELDS, " ) values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addConversation(Conversation conversation);

    @Select({"select ",SELECT_FIELDS , " from ", TABLE_NAME, " where id=#{id}"})
    Conversation getById(int id);

    @Select({"select ",SELECT_FIELDS , " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by created_date desc"})
    List<Conversation> getConversationDetail(@Param("conversationId") String conversationId);

    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConvesationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    //SELECT * ,COUNT(id) AS cnt FROM (SELECT * FROM conversation ORDER BY created_date DESC)
    // tt GROUP BY conversation_id ORDER BY created_date DESC;
    @Select({"select ",INSERT_FIELDS, " ,count(id) as cnt from ( select * from ",TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} order by created_date desc) tt group by conversation_id ORDER BY created_date DESC limit #{offset},#{limit} "})
    List<Conversation> getConversationList(@Param("userId") int user,@Param("offset") int offset,@Param("limit") int limit);

    @Update({"update ",TABLE_NAME," set has_read = 1 where id=#{id}"})
    void updateHasRead(int id);

}
