package com.nowcoder.controller;

import com.nowcoder.model.Conversation;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.ConversationService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    ConversationService conversationService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    /**
     * 发送信息
     * @param toName
     * @param content
     * @return
     */
    @RequestMapping(path = "/msg/addMessage",method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,@RequestParam("content") String content){
        try {
            if(hostHolder.getUser()==null){
                return Md5Utils.getJsonString(999,"未登录");
            }
            if(userService.getByName(toName)==null){
                return Md5Utils.getJsonString(1,"用户不存在");
            }

            Conversation conversation = new Conversation();
            conversation.setContent(content);
            conversation.setCreatedDate(new Date());
            conversation.setToId(userService.getByName(toName).getId());
            conversation.setFromId(hostHolder.getUser().getId());
            conversation.setHasRead(0);
            conversationService.addConversation(conversation);
            return Md5Utils.getJsonString(0);
        }catch (Exception e){
            e.printStackTrace();
            return Md5Utils.getJsonString(1,"发送失败");
        }
    }

    /**
     * 展示信息

     * @return
     */
    @RequestMapping(path = "/msg/list",method = RequestMethod.GET)
    public String getConversationList(Model model){
        try {
            if(hostHolder.getUser()==null){
                return "redirect:/reglogin";
            }
            int localUserId = hostHolder.getUser().getId();
            List<Conversation> conversationList=conversationService.getConversationList(localUserId,0,5);
            List<ViewObject> conversations = new ArrayList<>();
            for(Conversation conversation:conversationList){
                ViewObject vo = new ViewObject();
                vo.set("conversation",conversation);
                int targetId = conversation.getFromId() == localUserId ? conversation.getToId() :conversation.getFromId();
                vo.set("user",userService.getById(targetId));
                vo.set("unread",conversationService.getConvesationUnreadCount(localUserId,conversation.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations",conversations);
            return "letter";
        }catch (Exception e){
            e.printStackTrace();
        }


        return "letter";
    }
    @RequestMapping(path = "/msg/detail",method = RequestMethod.GET)
    public String getConversationDetial(Model model ,@RequestParam("conversationId") String conversationId){
        try {
            List<Conversation> messageList = conversationService.getConversationDetail(conversationId);
            List<ViewObject> messages = new ArrayList<>();
            for(Conversation conversation:messageList){
                //标记为已读
                conversationService.updateHasRead(conversation.getId());
                ViewObject vo = new ViewObject();
                vo.set("message",conversation);
                vo.set("headUrl",userService.getById(conversation.getFromId()).getHeadUrl());
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
            return "letterDetail";
        }catch (Exception e){
            return Md5Utils.getJsonString(1,"获取失败");
        }
    }

}
