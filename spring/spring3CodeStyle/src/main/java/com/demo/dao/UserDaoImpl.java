package com.demo.dao;

import org.springframework.stereotype.Component;

@Component("dao")
public class UserDaoImpl implements UserDao {

    @Override
    public void test() {
        System.out.println("test spring 3 code style");
    }
}
