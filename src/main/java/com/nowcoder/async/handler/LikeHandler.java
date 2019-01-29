package com.nowcoder.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikeHandler implements EventHandler{
    @Autowired
    MessageService messageService;
    
    @Autowired
    UserService userService;

    @Override
    public void doHandler(EventModel model){
        // 进行eventModel的异步处理
        Message message = new Message();
        message.setContent("用户："+ userService.getUser(model.getActorId())+"赞了你的评论，http://127.0.0.1/question/"+ model.getExts("questionId"));
        message.setFromId(WendaUtil.SYSYTEM_USERID);
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }
    @Override
    public List<EventType> getSupportEventTypes(){
        return Arrays.asList(EventType.LIKE);
    }
}