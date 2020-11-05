package com.demo.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TestJDKInvocationHandler implements InvocationHandler {

    Object target;
    public TestJDKInvocationHandler(Object target){
        this.target=target;
    }
    /**
     *
     * @param proxy 代理对象
     * @param method 目标对象
     * @param args    目标方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("JDK 方法增强");
        return method.invoke(target,args);
    }

}
