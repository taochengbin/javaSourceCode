package org.example;

import com.demo.dao.UserDao;
import com.demo.dao.UserDaoImpl;
import com.demo.util.ProxyUtils;
import com.demo.util.TestJDKInvocationHandler;
import org.junit.Test;

import java.lang.reflect.Proxy;

public class TestProxy {

    @Test
    public void test(){
        UserDao proxy = (UserDao) ProxyUtils.newInstance(new UserDaoImpl());
        proxy.testProxy();
    }

    @Test
    public void testJDkProxy(){
        UserDao jdkproxy = (UserDao) Proxy.newProxyInstance(Test.class.getClassLoader(),
            new Class[]{UserDao.class},new TestJDKInvocationHandler(new UserDaoImpl()));
        try {
            jdkproxy.testProxy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
