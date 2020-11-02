package com.demo.dao;

import com.demo.annotation.UserAnnotation;
import org.springframework.stereotype.Repository;

@Repository("UserDaoImpl")
public class UserDaoImpl implements UserDao{
    @Override
    public void testExecution() {
        System.out.println("这是dao业务逻辑----");
    }

    @Override
    public void testWithin() {
        System.out.println("testWithin:这是dao业务逻辑----");
    }

    @Override
    public void testArgs(String str) {
        System.out.println("testArgs:这是dao业务逻辑----");
    }

    @Override
    public void testTarget() {
        System.out.println("testTarget:这是dao业务逻辑----");
    }

    @Override
    @UserAnnotation(str = "aaaaa")
    public void testAnnotation() {
        System.out.println("testAnnotation:这是dao业务逻辑----");
    }
}
