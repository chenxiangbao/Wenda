package com.nowcoder.service;

import com.nowcoder.dao.ConversationDao;
import com.nowcoder.model.Conversation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {
    @Autowired
    ConversationDao conversationDao;

    public int addConversation(Conversation conversation){
        return conversationDao.addConversation(conversation);
    }

    public Conversation getById(int id){
        return conversationDao.getById(id);
    }

    public List<Conversation> getConversationDetail(String conversationId){
        return conversationDao.getConversationDetail(conversationId);
    }

    public int getConvesationUnreadCount(int userId,String conversationId){
        return conversationDao.getConvesationUnreadCount(userId,conversationId);
    }

    public  List<Conversation> getConversationList(int user,int offset, int limit){
        return conversationDao.getConversationList(user,offset,limit);
    }

    public void updateHasRead(int id){
        conversationDao.updateHasRead(id);
    }
}
