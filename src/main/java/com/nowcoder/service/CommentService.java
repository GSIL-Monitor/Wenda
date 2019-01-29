package com.nowcoder.service;

import java.util.List;

import com.nowcoder.dao.CommentDao;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class CommentService{
    @Autowired
    CommentDao commentDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addComment(Comment comment){
        // 也应该有个敏感词检查
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment);
    }

    public List<Comment> getCommentByEntity(int entityId,int entityType){
        // System.out.println("目前在commentService的getCommentByEntity之中："+ entityId + "||"+ entityType);
        return commentDao.getComments(entityId, entityType);
    }

    public boolean deleteComment(int id){
        return commentDao.updateCommentStatusById(1, id) > 0;
    }

    public int getCommengtsCount(int entityId,int entityType){
        return commentDao.getCommentsCount(entityId, entityType);
    }

    public Comment getCommentById(int id){
        return commentDao.getCommentById(id);
    }

    public int getUserCommentCount(int userId) {
        return commentDao.getUserCommentCount(userId);
    }
}