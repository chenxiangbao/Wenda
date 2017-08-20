package com.nowcoder.service;

import com.nowcoder.dao.QuestionDao;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;
    @Autowired
    SensitiveService sensitiveService;

    public int addQuestion(Question question){
        //过滤html标签
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));


        //敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        return questionDao.addQuestion(question);
    }

    public Question selectById(int id){
        return questionDao.selectById(id);
    }

    public void updateById(Question question){
        questionDao.updateById(question);
    }

    public void deleteById(int id){
        questionDao.deleteById(id);
    }

    public List<Question> selectAllQuestion(int offset,int limit){
        return questionDao.selectAllQuestion(offset,limit);
    }

    public List<Question> selectQuestionByUserId(int userId,int offset,int limit){
        return questionDao.selectQuestionByUserId(userId,offset,limit);
    }

    public void updateCount(int entityId,int count){
        questionDao.updateCount(entityId,count);
    }
}
