package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class QuestionController{
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path={"/question/add"},method={RequestMethod.POST})
    // 返回JSON传而不是页面了
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try {
            // 设立问题，调用问题服务将问题入库
            Question question =new Question();
            question.setCreatedDate(new Date());
            question.setContent(content);
            question.setTitle(title);
            if(hostHolder.getUser() == null){
                // 设置一个匿名用户的的Id
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }
            // 在入库过程，即调用Questionseivice时进行敏感词过滤操作
            if(questionService.addQuestion(question)>0){
                return WendaUtil.getJsonString(0);
            }
        } catch (Exception e) {
            logger.error("增加题目失败" + e.getMessage());

        }
        // return json串表示失败,xiamian 开始写在wendautils中写返回的JSON串;在response中返回
        return WendaUtil.getJsonString(1, "增加题目失败");
    }


    @RequestMapping(path="/question/{qid}",method={RequestMethod.GET})
     public String questionDetail(@PathVariable("qid") int qid,Model model){
         Question question = questionService.getQuestion(qid);
         if (question == null){
             return WendaUtil.getJsonString(301, "打开问题失败");
         }
         model.addAttribute("question",question);
        //  System.out.println("目前在questionController之前"+ qid);
         List<Comment> commentList = commentService.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
        //  System.out.println("目前在questionController之后");
         List<ViewObject> comments = new ArrayList<>();
         for (Comment comment:commentList){
             ViewObject vo = new ViewObject();
             vo.set("comment", comment);
             if (hostHolder.getUser() == null){
                 vo.set("liked", 0);
             }else{
                 vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), comment.getId(), EntityType.ENTITY_COMMENT));
             }
             long likeCount = likeService.getLikeCount( comment.getId(), EntityType.ENTITY_COMMENT);
             vo.set("likeCount", likeCount);
            //  除了设置评论信息。还要设置评论用户的相关信息
             vo.set("user", userService.getUser(comment.getUserId()));
             comments.add(vo);
         }
         model.addAttribute("comments", comments);
         return "detail";
    }
}