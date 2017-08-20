package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;


    @RequestMapping(path = "/question/add",method = RequestMethod.POST)
    @ResponseBody
    public String questionAdd(@RequestParam("title") String title,@RequestParam("content") String content){
        try {
            Question question = new Question();
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            if(hostHolder.getUser()==null){
                //匿名用户
//                int hostUserId = 3;
//                question.setUserId(hostUserId);
                return Md5Utils.getJsonString(999);
            }
            question.setUserId(hostHolder.getUser().getId());
            question.setCommentCount(0);
            questionService.addQuestion(question);
            return Md5Utils.getJsonString(0,"提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return Md5Utils.getJsonString(1,"提交失败");
        }
    }
    ///question/8
    @RequestMapping("/question/{qid}")
    public String questionDetail(@PathVariable("qid") int qid,Model model){

        Question question = questionService.selectById(qid);
        List<Comment> commentList = commentService.getCommentByEntityId(qid,0,10);
        List<ViewObject> vos = new ArrayList<>();
        for(Comment comment:commentList){
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            if(hostHolder.getUser()==null){
                vo.set("liked",0);
            }else{
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_NEWS,comment.getId()));
            }
            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_NEWS,comment.getId()));
            vo.set("user",userService.getById(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments",vos);
        model.addAttribute("question",question);
        model.addAttribute("user",userService.getById(question.getUserId()));
        return "detail";
    }


}
