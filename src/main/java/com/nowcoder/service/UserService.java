package com.nowcoder.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.nowcoder.dao.LoginTicketDao;
import com.nowcoder.dao.UserDao;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService{
    @Autowired
    UserDao userDao;

    @Autowired
    LoginTicketDao loginTicketDao;

    public int addUser(User user){
        return userDao.addUser(user);
    }

    public User getUser(int id){
        return userDao.selectById(id);
    }

    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<>();
        // 首先提供基本输入字符的判断
        if(StringUtils.isBlank(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        // 然后对账号密码的存在性判断,调用userDao来判断
        User user = userDao.selectByName(username);
        
        if (user == null){
            map.put("msg", "用户名不存在");
            return map;
        }

        // System.out.println("登录操作******************************");  
        // System.out.println("登录sql_salt:"+ user.getSalt());    
        // System.out.println("登录sql密码:"+ user.getPassword());        
        // System.out.println("自己输入的密码:"+ password);  
        // System.out.println("自己输入的密码+salt经过MD5:"+ WendaUtil.MD5(password+user.getSalt()));
        // System.out.println(user.getPassword() + "||" + WendaUtil.MD5(password+user.getSalt()));
        // System.out.println("结束登录操作******************************");  
        

        // 然后对密码正确性判断,注意数据库中使用的是MD5加密过后的密码，所以不能直接用equals判断。先写个MD5算法（wendautil中），在写注册功能的时候会用到
        // equals和==傻傻分不清楚！！！！！！！！！！！！！！！！！！！！！！！！！！！！！这里用equals不行，用==可以：==判断内存地址是否相同，equals判断对象内容是否相同？？？在这里有问题啊。。。
        if (user.getPassword()== WendaUtil.MD5(password+user.getSalt())){
            System.out.println("数据库密码与输入密码不一致");
            map.put("msg", "密码不正确");
            return map;
        }
        map.put("token", addLoginTicket(user.getId()));
        map.put("userId", user.getId());
        return map;
    }

    // 下面写注册功能
    public Map<String,Object> register(String username,String password){
        Map<String,Object> map = new HashMap<>();
        // 首先提供基本输入字符的判断
        if(StringUtils.isBlank(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        // 下面应该判断用户名是否已经存在
        User user =userDao.selectByName(username);
        if (user != null){
            map.put("msg","用户名已存在");
            return map;
        }
        User regUser = new User();
        String headUrl = String.format("http://inages.nowcoder.com/head/%dt", new Random().nextInt(1000));
        regUser.setName(username);
        regUser.setSalt(UUID.randomUUID().toString().substring(0, 5));
        // System.out.println("注册操作******************************");  
        // System.out.println("salt:"+ regUser.getSalt());
        regUser.setPassword(WendaUtil.MD5(password + regUser.getSalt()));
        // System.out.println("sqlPassword:"+ regUser.getPassword());
        regUser.setHeadUrl(headUrl);
        userDao.addUser(regUser);
        // System.out.println("结束注册操作******************************");  
        map.put("token", addLoginTicket(regUser.getId()));
        map.put("userId", regUser.getId());        
        return map;
    }

    // 加ticket服务
    public String addLoginTicket(int userId){
        LoginTicket t = new LoginTicket();
        Date date = new Date();
        date.setTime(date.getTime() + 7*24*1000*60*60);
        // System.out.println(date.toGMTString());
        t.setUserId(userId);
        t.setStatus(0);
        t.setExpired(date);
        t.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDao.addTicket(t);
        return t.getTicket();
    }

    public void logout(String ticket){
        loginTicketDao.updateStatus(1, ticket);
    }

    public User selectByName(String name){
        return userDao.selectByName(name);
    }
}