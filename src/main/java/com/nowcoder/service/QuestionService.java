package com.nowcoder.service;

import java.util.List;

import com.nowcoder.dao.QuestionDao;
import com.nowcoder.model.Question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class QuestionService{
    @Autowired
    QuestionDao questionDao;

    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getQuestions(int userId,int offset,int limit){
        return questionDao.selectLatestQuestions(userId, offset, limit);
    } 

    public int addQuestion(Question question){
        // 在入库之前进行敏感词过滤:先进行基础的html标签过滤
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        // 在入库之前进行敏感词过滤:然后进行文本敏感词过滤
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        return questionDao.addQuestion(question) > 0? question.getId():0;
    }

    public Question getQuestion(int qid){
        System.out.println(questionDao.getQuestionById(qid).getTitle());
        return questionDao.getQuestionById(qid);
    }

    public int updateCommentCount(int commentCount, int id){
        return questionDao.updateCommentCount(commentCount, id);
    }

}