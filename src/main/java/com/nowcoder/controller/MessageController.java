package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageController{
    private static final Logger logger = LoggerFactory.getLogger(LoginController1.class);
    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/list"},method ={RequestMethod.GET})
    public String ConversationList(Model model){
        try {
            User user = hostHolder.getUser();
            List<Message> conversationList = messageService.getConversationList(user.getId(), 0, 10);
            List<ViewObject> conversations = new ArrayList<>();
            for (Message message:conversationList){
                // 不应该在这写这个，因为这相当于在干创建message的活，不应该在这里干
                // String conversationId = user.getId() < message.getFromId()? String.format("%d_%d", (user.getId(),message.getFromId()))
                //                                                             :String.format("%d_%d", (message.getFromId(),user.getId()));
                int targetId = user.getId() == message.getFromId()? message.getToId():message.getFromId();
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                vo.set("user", userService.getUser(targetId));
                vo.set("unread", messageService.getConversationUnreadCount(user.getId(), message.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("消息列表获取失败"+ e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"},method ={RequestMethod.GET})
    public String conversationDetail(Model model,@RequestParam("conversationId") String conversationId){
        try {
            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message:messageList){
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                vo.set("user", userService.getUser(message.getFromId()));
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            logger.error("获取详情失败"+e.getMessage());
        }
        return "letterDetail";
    }
    
    // 又是一个弹窗页面，没给我写弹窗，但是要记住返回JSON串而非网页
    @RequestMapping(path = {"/msg/addMessage"},method ={RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,@RequestParam("content") String content){
        try {
            if (hostHolder.getUser() == null){
                return WendaUtil.getJsonString(999,"未登录");
            }
            User user = userService.selectByName(toName);
            if (user == null){
                return WendaUtil.getJsonString(1, "用户不存在");
            }
                        Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setHasRead(0);
            message.setToId(user.getId());
            String conversationId = message.getFromId() < message.getToId()? String.format("%d_%d", message.getFromId(),message.getToId())
                                                                            :String.format("%d_%d", message.getToId(),message.getFromId());
            message.setConversationId(conversationId);
            messageService.addMessage(message);
            return WendaUtil.getJsonString(0);
        } catch (Exception e) {
            logger.error("发送消息失败"+ e.getMessage());
            return WendaUtil.getJsonString(1,"发信失败");
        }
    }

}