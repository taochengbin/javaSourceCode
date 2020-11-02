package com.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.demo")
@EnableAspectJAutoProxy(proxyTargetClass=false)
public class AspectjConfig {
}
