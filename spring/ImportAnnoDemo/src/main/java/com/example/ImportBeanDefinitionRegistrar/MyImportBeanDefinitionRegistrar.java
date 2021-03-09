package com.example.ImportBeanDefinitionRegistrar;

import com.example.daoImpl.UserDaoImpl3;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(UserDaoImpl3.class);
        rootBeanDefinition.setScope("prototype");
        registry.registerBeanDefinition("dao3333",rootBeanDefinition);

    }

//    @Override
//    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
//        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(UserDaoImpl3.class);
//        rootBeanDefinition.setScope("prototype");
//        registry.registerBeanDefinition("dao3333",rootBeanDefinition);
//    }
}
