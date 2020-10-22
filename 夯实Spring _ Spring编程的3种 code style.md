@[toc]
##  前言
此文主要是为了对spring编程的一个回顾以及总结，为后续spring源码学习夯实基础。主要阐述spring编程的三种编程风格：

- schemal-based-------基于xml配置文件
- annotation-based-----基于annotation注解
- java-based----java Configuration（springboot）

首先看一看spring官方文档：
[https://spring.io/projects/spring-framework](https://spring.io/projects/spring-framework)
[https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201022155058792.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/202010221609212.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)


无论是工作还是面试，spring中的ioc，di， aop，都是高频词，也是重中之重。然而很多面试者答到spring是什么，还是不少人会认为，spring就是ioc，aop。然而这种回答是错误的，从官方文档中就可以看出ioc，aop只是属于Spring Framework的其中一块知识点。
##  schemal-based  基于xml配置文件
基于配置文件容器管理bean，首先搭建一个简单的demo：
1.编写dao接口和daoimpl实现类

```
public interface UserDao  {

    public void test();

}
```

```
public class UserDaoImpl implements UserDao {

    @Override
    public void test() {
        System.out.println("test spring 3 code style");
    }

}
```
2.编写service接口和service实现类，这里通过构造器注入userDao

> 这里做个知识点补充：
> spring3 中依赖注入三种方式：构造器，set，接口注入
> spring4 取消了接口注入，因为不人性化

```
public interface UserService {
    public void testService();
}
```

```
public class UserServiceImpl implements UserService{

    private UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void testService() {
        userDao.test();
    }


}
```
3.pom文件配置加入依赖

```
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>4.3.2.RELEASE</version>
    </dependency>
```
4.spring.xml文件

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.0.xsd
        http://activemq.apache.org/schema/core http://activemq.apache.org/schema/core/activemq-core.xsd
        http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-4.0.xsd">

  <bean id="userDao" class="com.demo.dao.UserDaoImpl"></bean>

  <bean id="userService" class="com.demo.service.UserServiceImpl">
    <constructor-arg name="userDao" ref="userDao"></constructor-arg>
  </bean>

</beans>
```
5.编写测试类执行dao里的方法打印test spring 3 code style，验证容器是否注入管理bean

```
public class TestDemo {
    @org.junit.Test
    public  void test1() {
        //1. 基于xml获取bean
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        UserServiceImpl userServiceImpl = (UserServiceImpl) classPathXmlApplicationContext.getBean("userService");
        userServiceImpl.testService();

    }
}
```
6.测试结果如下：
可看出在xml文件配置的bean，通过容器创建且执行了对应的方法

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201022162958722.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
## annotation-based 基于annotation注解
1.UserServiceImpl实现类上添加@Service注解，userDao添加@Autowired注解自动注入

```
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void testService() {
        userDao.test();
    }

}
```
2.dao实现类添加@Component("dao")注解

```
@Component("dao")
public class UserDaoImpl implements UserDao {

    @Override
    public void test() {
        System.out.println("test spring 3 code style");
    }
}
```
3.spring.xml配置文件删除配置的bean，添加自动扫描com.demo包下面的类
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.0.xsd
        http://activemq.apache.org/schema/core http://activemq.apache.org/schema/core/activemq-core.xsd
        http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-4.0.xsd">

  <context:annotation-config></context:annotation-config>
  <context:component-scan base-package="com.demo"/>

</beans>
```
这里对annotation-config，component-scan做个说明：

>spring3之后的某个版本合并开启对注解的支持annotation-config和开启对注解的扫描功能component-scan
> <context:annotation-config>:注解扫描是针对已经在Spring容器里注册过的Bean
> <context:component-scan>:不仅具备<context:annotation-config>的所有功能，还可以在指定的package下面扫描对应的bean
> <context:annotation-config />和 <context:component-scan>同时存在的时候，前者会被忽略。
> 即使注册Bean，同时开启<context:annotation-config />扫描，@autowire，@resource等注入注解只会被注入一次，也即只加载一次

4.编写测试方法2执行dao里的方法打印test spring 3 code style，验证容器是否注入管理bean

```
    @Test
    public void test2(){
        //2. 基于注解获取bean
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        UserServiceImpl userServiceImpl = (UserServiceImpl) classPathXmlApplicationContext.getBean("userServiceImpl");
        userServiceImpl.testService();
    }

```
由图可知测试成功，注意一下这里获取的bean是UserServiceImpl，自动扫描默认创建的bean就是以类名创建的。而test1xml文件方式创建的bean是userService，可得注解方式是成功的

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201022164224966.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
##  Java-based 基于 java Configuration（spring boot中常见）
1.新建SpringConfig类用于等价于spring.xml的作用，删除spring.xml文件

```
@Configuration
@ComponentScan("com.demo")
public class SpringConfig {
}
```
2.编写test3方法测试，这里由于没有了spring.xml文件，所以采用AnnotationConfigApplicationContext类读取SpringConfig配置文件

```
    @Test
    public void test3(){
        //3. Java-based 基于 java Configuration
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        UserServiceImpl userServiceImpl = (UserServiceImpl) annotationConfigApplicationContext.getBean("userServiceImpl");
        userServiceImpl.testService();
    }
```
由图可知，测试成功。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201022164910772.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
==注意：这3三种方式可以独立使用，也可以混合使用。相互之间不冲突的。==
例如：
1.重新建spring.xml文件，只配置bean

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
        http://www.springframework.org/schema/aop `在这里插入代码片`http://www.springframework.org/schema/aop/spring-aop-4.0.xsd
        http://activemq.apache.org/schema/core http://activemq.apache.org/schema/core/activemq-core.xsd
        http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-4.0.xsd">

  <bean id="userDao" class="com.demo.dao.UserDaoImpl">

  </bean>

  <bean id="userService" class="com.demo.service.UserServiceImpl">
    <constructor-arg name="userDao" ref="userDao"></constructor-arg>

  </bean>


</beans>

```
2.SpringConfig 类添加@ImportResource("classpath:spring.xml")注解指定配置文件

```
@Configuration
@ComponentScan("com.demo")
@ImportResource("classpath:spring.xml")
public class SpringConfig {
}
```
3.编写test4方法测试

```
    @Test
    public void test4(){
        //4. Java-based与xml结合使用
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        UserServiceImpl userServiceImpl = (UserServiceImpl) annotationConfigApplicationContext.getBean("userService");
        userServiceImpl.testService();
    }
```
测试结果如图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201022165727812.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
##  总结
1. spring中基于xml，注解，Java-based三种风格实现容器对bean的创建管理
2. 这三种风格可以随意搭配结合使用互不影响
3. 只有对一门框架和技术融会贯通，才能在工作中得心应手






