package com.example.daoImpl;

import com.example.ImportSelector.MyInvocationHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Proxy;

public class UserDaoImpl2 implements BeanPostProcessor,UserDao {

    @Override
    public void test() {
        System.out.println("bean：UserDaoImpl2");
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("com.example.daoImpl.UserDaoImpl1")){
            bean = Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{UserDao.class},new MyInvocationHandler(bean));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
