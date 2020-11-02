package com.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Aspectj {

    // test Execution
    @Pointcut("execution(* com.demo.dao.UserDao.testExecution())")
    public void pointCutExecution(){
        System.out.println("pointcut Execution----");
    }

    // test within
    @Pointcut("within(com.demo.dao.*)")
    public void pointCutWithin(){
        System.out.println("pointcut Within----");
    }

    // test args
    @Pointcut("args(java.lang.String)")
    public void pointCutArgs(){
        System.out.println("pointcut args----");
    }

    // test target
    @Pointcut("target(com.demo.dao.UserDaoImpl)")
    public void pointCutTarget(){
        System.out.println("pointcut args----");
    }

    // test @annotation
    @Pointcut("@annotation(com.demo.annotation.UserAnnotation)")
    public void pointCutAnnotation(){
        System.out.println("pointcut args----");
    }

    // 1.Execution
    @Before("pointCutExecution()")
    public void beforeExecution(){
        System.out.println("testExecution: 前置通知");
    }

    @Around("pointCutExecution()")
    public void aroundExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("testExecution: 方法执行前增强...");
        // 执行目标方法
        Object object=proceedingJoinPoint.proceed();

        System.out.println("testExecution: 方法执行后...");
    }

    @After("pointCutExecution()")
    public void afterExecution(){
        System.out.println("testExecution: 后置通知");
    }

    // 2.within
    @Before("pointCutWithin()")
    public void beforeWithin(){
        System.out.println("testwithin: 前置通知");
    }

    // 3.args
    @Before("pointCutArgs()")
    public void beforeArgs(){
        System.out.println("args: 前置通知");
    }

    // 4.target
    @Before("pointCutTarget()")
    public void beforeTarget(){
        System.out.println("Target: 前置通知");
    }

    // 5.Annotation
    @Before("pointCutAnnotation()")
    public void beforeAnnotation(){
        System.out.println("Annotation: 前置通知");
    }
}
