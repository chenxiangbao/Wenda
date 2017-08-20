package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/reg"},method = {RequestMethod.POST})
    public String regist(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         Model model,
                         HttpServletResponse response){
        try {
            Map<String,String> map = userService.regist(username,password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "login";
    }

    @RequestMapping(path = {"/login"},method = {RequestMethod.POST})
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletResponse response,
                        @RequestParam(value = "next", required = false) String next,
                        Model model){
        try {
            Map<String,String> map = userService.login(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if(!StringUtils.isBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "login";
    }

    @RequestMapping(path = "/loginout",method = RequestMethod.GET)
    public String loginout(@CookieValue("ticket") String ticket){

        userService.logout(ticket);

        return "redirect:/";
    }

    @RequestMapping("/user/{id}")
    public String getUserIdQuestion(@PathVariable int id,Model model){
        if(hostHolder.getUser()!=null){
            model.addAttribute("user",hostHolder.getUser());
        }

        List<Question> questionList =questionService.selectQuestionByUserId(id,0,5);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question:questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getById(question.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("vos",vos);
        return "index";
    }

    @RequestMapping(path = "/reglogin",method = RequestMethod.GET)
    public String reglogin(@RequestParam(value = "next",required = false)String next,Model model){
        model.addAttribute("next", next);
        return "login";
    }
}
