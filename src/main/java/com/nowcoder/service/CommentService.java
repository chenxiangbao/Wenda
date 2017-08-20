package com.nowcoder.service;

import com.nowcoder.dao.CommentDao;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDao commentDao;
    @Autowired
    SensitiveService sensitiveService;
    public int addComment(Comment comment){
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //敏感词过滤
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment);
    }

    public List<Comment> getCommentByEntityId(int entityId, int offset, int limit){
        return commentDao.getCommentByEntityId(entityId,offset,limit);
    }
    public int getUserCommentCount(int userId) {
        return commentDao.getUserCommentCount(userId);
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDao.getCommentCount(entityId,entityType);
    }

    public boolean deleteComment(int id,int status){
        return commentDao.updateComment(id,status);
    }

    public Comment getByCommentId(int id){
        return commentDao.getByCommentId(id);
    }
 }
