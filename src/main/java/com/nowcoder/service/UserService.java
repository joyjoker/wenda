package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 无论登录或注册用户信息验证完了以后立即登记一个ticket
 * 一注册完立刻就登陆 表明一个人性化的操作
 * 在前台Controller中通过HttpServletResponse来下发
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    //调用DAO层返回id查找的用户
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    //增加一个注册用户的方法
    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        //判断用户名密码是否为空
        //三个if里都有return语句所以只会有一个msg存在
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }
        //然后判断用户名是否已经存在
        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msg", "用户名已经被注册");
            return map;
        }

        //所有的检查完毕,将用户注册进来
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        //添加用户
        userDAO.addUser(user);
        //添加ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }
    //增加一个登陆的方法
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        //判断用户名密码是否为空
        //三个if里都有return语句所以只会有一个msg存在
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }
        //然后判断用户名是否已经存在
        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        //判断密码是否正确
        if (!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg", "密码不正确");
            return map;
        }
        //登录,把ticket和用户关联起来
        String ticket = addLoginTicket(user.getId());
        //把ticket传到外面
        map.put("ticket", ticket);
        return map;
    }

    //生成ticket并添加到数据库中
    public String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        //设置过期时间
        Date date = new Date();
        //一天后过期
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replace("-",""));
        //把ticket添加到数据库中
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }



    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    //登出,直接将status设置为1
    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }
}
