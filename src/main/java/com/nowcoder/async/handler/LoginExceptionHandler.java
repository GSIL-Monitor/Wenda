package com.nowcoder.async.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.service.UserService;
import com.nowcoder.util.MailSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginExceptionHandler implements EventHandler{
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandler(EventModel model){
        Map<String,Object> map = new HashMap<>();
        map.put("username", model.getExts("username"));
        mailSender.sendWithHTMLTemplate(model.getExts("email"), "登录IP异常", "mails/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes(){
        return Arrays.asList(EventType.LOGIN);
    }

}