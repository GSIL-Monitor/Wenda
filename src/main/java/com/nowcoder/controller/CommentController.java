package com.nowcoder.controller;

import java.util.Date;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.util.WendaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CommentController{
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path ={"/addComment"},method={RequestMethod.POST})
    public String addComment(Model model,@RequestParam("content") String content,@RequestParam("questionId") int qid){
        try {
            if (hostHolder.getUser() == null){
                return "redirect:/reglogin";
            }
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setEntityId(qid);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setStatus(0);
            comment.setUserId(hostHolder.getUser().getId());
            commentService.addComment(comment);

            // 顺便更新评论数目,用现在设置的来更新，而不是用外部数据来更新！！
            int commentCount = commentService.getCommengtsCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(commentCount, qid);
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
         return "redirect:/question/" + qid;
    }
    // 添加一条评论，然后还要把评论显示，在questionController里面实现

}