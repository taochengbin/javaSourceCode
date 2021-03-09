package com.example.ImportSelector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {

	Object target;

	public MyInvocationHandler(Object object){
		this.target = object;

	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("proxy-------------");
		return method.invoke(target,args);
	}
}
