package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.WendaService;
import com.nowcoder.util.WendaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController{
    private static final Logger logger = LoggerFactory.getLogger(LoginController1.class);
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/like"},method ={RequestMethod.GET})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJsonString(999, "用户未登录");
        }
        Comment comment = commentService.getCommentById(commentId);
        long likeCount = likeService.like(hostHolder.getUser().getId(), commentId, EntityType.ENTITY_COMMENT);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId()).setEntityId(commentId).
                                setEntityOwnerId(comment.getUserId()).setEntityType(EntityType.ENTITY_COMMENT).
                                setType(EventType.LIKE).setExts("questionId",String.valueOf(comment.getEntityId())));
        // 用户的实时以ajax形式动态返回点赞数，所以变量不在detail.html中，只有打开该问题时才会显示该变量
        return WendaUtil.getJsonString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"},method ={RequestMethod.GET})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJsonString(999, "用户未登录");
        }
        long likeCount = likeService.dislike(hostHolder.getUser().getId(), commentId, EntityType.ENTITY_COMMENT);
        // 用户的实时以ajax形式动态返回点赞数，所以变量不在detail.html中，只有打开该问题时才会显示该变量
        return WendaUtil.getJsonString(0, String.valueOf(likeCount));
    }

}