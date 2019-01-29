package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService{
    @Autowired
    JedisAdapter jedisAdapter;

    // 首先明确业务：like和dislike都要返回更新后的dlike总数，getlikeStatus要返回个对某一事物的like or not情况：getlikeCOunt表示不是对user的操作。而是“自然状态下”的事物的喜欢数
    public long like(int userId,int entityId,int entityType){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String disLikeKey =RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public long dislike(int userId,int entityId,int entityType){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String disLikeKey =RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public int getLikeStatus(int userId,int entityId,int entityType){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        // 在不喜欢set中返回-1，否则为0（没评判状态）
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId))? -1:0;
    }

    public long getLikeCount(int entityId,int entityType){
        String LikeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        // 在不喜欢set中返回-1，否则为0（包含喜欢和没评判状态）
        return jedisAdapter.scard(LikeKey);
    }

}