package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDao;
import com.nowcoder.dao.UserDao;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    LoginTicketDao loginTicketDao;

    public int addUser(User user){
       return  userDao.addUser(user);
    }

    public Map<String,String> regist(String username,String password){
        Map<String,String> map = new HashMap<>();
        if(username==null || password==null){
            map.put("msg","用户名和密码不能为空");
            return map;
        }
        User user = new User();
        if( userDao.getByName(username)!=null){
            map.put("msg","用户名已存在");
            return map;
        }
        user.setName(username);
        String salt = UUID.randomUUID().toString().substring(0,5);
        user.setSalt(salt);
        user.setPassword(Md5Utils.getMD5(password+salt));
        user.setHeadUrl("http://images.nowcoder.com/head/722t.png");
        userDao.addUser(user);
        //添加token
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    public Map<String,String > login(String username,String password){
        Map<String,String> map = new HashMap<>();
        if(username==null || password==null){
            map.put("msg","用户名和密码不能为空");
            return map;
        }
        User user = userDao.getByName(username);
        if(user==null){
            map.put("msg","用户名不存在");
            return map;
        }
        if(!user.getPassword().equals(Md5Utils.getMD5(password+user.getSalt()))){
            map.put("msg","用户名或者密码错误");
            return map;
        }

        //添加token
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;

    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        //loginTicketDAO.addTicket(ticket);
        loginTicketDao.addLoginTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDao.updateByTicket(ticket);
    }

    public User getById(int id){
        return userDao.getById(id);
    }

    public User getByName(String username){
        return userDao.getByName(username);
    }

    public void deleteById(int id){
        userDao.deleteById(id);
    }

    public void updateById(User user){
        userDao.updateById(user);
    }
}
