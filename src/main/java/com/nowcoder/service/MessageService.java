package com.nowcoder.service;

import java.util.List;

import com.nowcoder.dao.MessageDao;
import com.nowcoder.model.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// 这里仅仅做的是站内信（用户之间的直接发消息），赞踩都不包括在站内信中，之后做
public class MessageService{
    @Autowired
    MessageDao messageDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message);
    }

    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageDao.getConversationList(userId, offset, limit);
    }

    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDao.getConversationDetail(conversationId, offset, limit);
    }

    public int getConversationUnreadCount(int userId,String conversationId){
        return messageDao.getConversationUnreadCount(userId, conversationId);
    }
}