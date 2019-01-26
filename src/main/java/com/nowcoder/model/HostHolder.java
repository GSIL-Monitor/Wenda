package com.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder{
    // 类似"threadLocal"型的数组，每个user都有一个thread来处理
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public  void clear(){
        users.remove();
    }

}