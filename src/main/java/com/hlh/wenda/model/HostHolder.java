package com.hlh.wenda.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    //为每一个线程分配一个对象（多线程）
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
