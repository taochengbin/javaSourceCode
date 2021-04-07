package com.demo.beaninit;

import com.demo.UserDao;
import org.springframework.stereotype.Component;

@Component("index")
public class UserDaoImpl4 implements UserDao {

	@SuppressWarnings("rawtypes")
	Class clazz;

	@SuppressWarnings("rawtypes")
	public UserDaoImpl4(Class clazz){
		this.clazz = clazz;
	}

	@Override
	public void test() {
		System.out.println(this.clazz);

	}
}
