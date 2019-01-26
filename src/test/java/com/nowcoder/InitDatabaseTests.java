package com.nowcoder;

import java.util.Random;

import com.nowcoder.dao.QuestionDao;
import com.nowcoder.dao.UserDao;
import com.nowcoder.model.User;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=WendaApplication.class)
public class InitDatabaseTests{
    @Autowired
    UserDao userDAO;

    @Autowired
    QuestionDao questionDAO;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Test
    public void contextLoads(){
        Random random = new Random();
        for (int i = 12; i < 21; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i+1));
            user.setPassword("xx");
            user.setSalt("");
            userDAO.addUser(user);
            
        }
    }

}