package com.demo;

import com.demo.importdemo.MyImport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ComponentScan("com.demo")
@MyImport
@Configuration
public class AppConfig {

//	@Bean
//	public UserDaoImpl userDao1(){
//		return new UserDaoImpl();
//	}

	//static会注入 2个bean
	@Bean
	public static UserDaoImpl userDao1(){
		return new UserDaoImpl();
	}

	@Bean
	public UserDaoImpl3 userDao3(){
		userDao1();
		return new UserDaoImpl3();
	}
}
