package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
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
public class FollowController{
    private static final Logger logger = LoggerFactory.getLogger(LoginController1.class);

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @RequestMapping(path ={"/followUser"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        // userId是要follow的用户的Id
        if (hostHolder.getUser() == null){
            return WendaUtil.getJsonString(999, "用户未登录");
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId()).setEntityId(userId).setEntityType(EntityType.ENTITY_USER)
                                                .setType(EventType.FOLLOW).setEntityOwnerId(userId));

        // 返回当前用户（hostHolder.getUser().getId()）关注的人数
        return WendaUtil.getJsonString(ret? 0:1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(path ={"/unfollowUser"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        // userId是要follow的用户的Id
        if (hostHolder.getUser() == null){
            return WendaUtil.getJsonString(999, "用户未登录");
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId()).setEntityId(userId).setEntityType(EntityType.ENTITY_USER)
                                                .setType(EventType.UNFOLLOW).setEntityOwnerId(userId));
                                                
        // 返回当前用户（hostHolder.getUser().getId()）关注的人数
        return WendaUtil.getJsonString(ret? 0:1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(path ={"/followQuestion"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        // userId是要follow的用户的Id
        if (hostHolder.getUser() == null){
            return WendaUtil.getJsonString(999, "用户未登录");
        }

        Question q = questionService.getQuestion(questionId);
        if (q == null){
            return WendaUtil.getJsonString(1, "问题不存在");
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId()).setEntityId(questionId).setEntityType(EntityType.ENTITY_QUESTION)
                                                .setType(EventType.FOLLOW).setEntityOwnerId(questionId));
        
        // 点击关注后将用户的信息已JSON串的形式返回给用户（页面的response中存有以下信息）
        Map<String,Object> info = new HashMap<>();
        // 关注一个问题为什么要返回当前用户的信息呢？直接返回一个问题的关注数不就行了么？：随具体业务而定
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJsonString(ret ? 0 : 1, info);
    }

    @RequestMapping(path ={"/unfollowQuestion"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        // userId是要follow的用户的Id
        if (hostHolder.getUser() == null){
            return WendaUtil.getJsonString(999, "用户未登录");
        }

        Question q = questionService.getQuestion(questionId);
        if (q == null){
            return WendaUtil.getJsonString(1, "问题不存在");
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId()).setEntityId(questionId).setEntityType(EntityType.ENTITY_QUESTION)
                                                .setType(EventType.UNFOLLOW).setEntityOwnerId(questionId));
        
        // 点击关注后将用户的信息已JSON串的形式返回给用户（页面的response中存有以下信息）
        Map<String,Object> info = new HashMap<>();
        // 关注一个问题为什么要返回当前用户的信息呢？直接返回一个问题的关注数不就行了么？：随具体业务而定
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJsonString(ret ? 0 : 1, info);
    }

    @RequestMapping(path ={"/user/{uid}/followers"},method={RequestMethod.GET})
    public String followers(Model model,@PathVariable("uid") int userId){
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser() != null){
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(),followerIds));
        }else{
            model.addAttribute("followers", getUsersInfo(0,followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        // 拿到用户自己的信息
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    @RequestMapping(path ={"/user/{uid}/followees"},method={RequestMethod.GET})
    public String followees(Model model,@PathVariable("uid") int userId){
        List<Integer> followerIds = followService.getFollowees(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser() != null){
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(),followerIds));
        }else{
            model.addAttribute("followees", getUsersInfo(0,followerIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
        // 拿到用户自己的信息
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId,List<Integer> userIds){
        List<ViewObject> usersInfo = new ArrayList<>();
        for (Integer uid:userIds){
            User user = userService.getUser(uid);
            if (user == null){
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            // 业务还要显示用户每个粉丝的：粉丝数、关注数、回答数、赞同数
            vo.set("commentCount",commentService.getUserCommentCount(uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            // localuserId:当前访问用户主页的Id,这个主要用在查看自己的关注上（取不取关，高亮不高亮的事儿）
            if (localUserId != 0){
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            }else{
                vo.set("followed", false);
            }
            usersInfo.add(vo);
        }
        return usersInfo;
    }
}