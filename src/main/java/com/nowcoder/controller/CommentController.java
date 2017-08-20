package com.nowcoder.controller;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class CommentController {

    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @RequestMapping("/addComment")
    public String addComment(HttpServletRequest request,
                             @RequestParam("content") String content,
                             @RequestParam("questionId") int questionId){
        Comment comment = new Comment();
        comment.setCreatedDate(new Date());
        if(hostHolder.getUser()==null){
            //没登录不准评论
            //也可以使用匿名评论

            return "login";
        }
        comment.setUserId(hostHolder.getUser().getId());
        comment.setEntityId(questionId);
        comment.setStatus(0);
        comment.setEntityType(EntityType.ENTITY_NEWS);
        comment.setContent(content);
        commentService.addComment(comment);

        int count =commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
        questionService.updateCount(comment.getEntityId(),count);
        return "redirect:/question/"+questionId;
    }
}
