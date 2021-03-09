package com.example.demo;

import com.example.ImportBeanDefinitionRegistrar.AppImportRegistrarConfig;
import com.example.ImportNormalClass.AppConfig;
import com.example.ImportSelector.AppSelectorConfig;
import com.example.daoImpl.UserDao;
import com.example.daoImpl.UserDaoImpl1;
import com.example.daoImpl.UserDaoImpl2;
import com.example.daoImpl.UserDaoImpl3;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class ImportTest {

    @Test
    public void testImportNormalClass() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppConfig.class);
        context.refresh();

        UserDaoImpl1 dao1 = context.getBean(UserDaoImpl1.class);
        dao1.test();

        UserDaoImpl2 dao2 = context.getBean(UserDaoImpl2.class);
        dao2.test();

    }

    @Test
    public void testImportSelector1() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppSelectorConfig.class);
        context.refresh();

        UserDaoImpl2 dao2 = context.getBean(UserDaoImpl2.class);
        dao2.test();

    }

    @Test
    public void testImportSelector2() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppSelectorConfig.class);
        context.refresh();

        UserDao dao = (UserDao) context.getBean("com.example.daoImpl.UserDaoImpl1");
        dao.test();

        UserDaoImpl2 userDaoImpl2 = context.getBean(UserDaoImpl2.class);
        userDaoImpl2.test();

    }


    @Test
    public void testImportBeanDefinitionRegistrar() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppImportRegistrarConfig.class);
        context.refresh();

        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println(name);
        }

        UserDaoImpl3 dao3 = (UserDaoImpl3) context.getBean("dao3333");
        dao3.test();

    }
}
