package com.demo.importdemo;

import com.demo.UserDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Proxy;

public class UserDaoImpl2 implements BeanPostProcessor{

	@SuppressWarnings({"unchecked","rawtypes"})
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (beanName.equals("userDaoImpl")){
			bean = Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{UserDao.class},new MyInvocationHandler(bean));
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

//	public void test(){
//		System.out.println("UserDaoImpl2---------------");
//	}
}
