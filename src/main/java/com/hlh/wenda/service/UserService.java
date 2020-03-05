package com.hlh.wenda.service;

import com.hlh.wenda.Util.WendaUtil;
import com.hlh.wenda.dao.LoginTicketDAO;
import com.hlh.wenda.dao.UserDAO;
import com.hlh.wenda.model.HostHolder;
import com.hlh.wenda.model.LoginTicket;
import com.hlh.wenda.model.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    HostHolder hostHolder;

    public Map<String,String> register(String username, String password){
        Map<String,String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);//查询用户名是否存在
        if(user != null){
            map.put("msg","用户名已经被注册");
            return map;
        }

        user = new User();//正式创建用户
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcode.com/head/%dt.png",
                new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        String ticket =  addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    public Map<String,String> login(String username, String password){
        Map<String,String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msg","用户名不存在");
            return map;
        }

        if(!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }

        String ticket =  addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600*24*100 + now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(loginTicket);

        return loginTicket.getTicket();

    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }
}
