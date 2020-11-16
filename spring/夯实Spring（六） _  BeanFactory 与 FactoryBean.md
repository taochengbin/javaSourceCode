@[toc]
##  概述
1. BeanFactory是一个接口，它是spring中的一个工厂，能够生产bean，获取bean，也就是IOC容器或对象工厂。
2. FactoryBean也是一个接口，实现了3个方法，通过重写其中的getObject()方法自定义生成bean逻辑创建一个新的bean，为IOC容器中Bean的实现提供了更加灵活的方式，FactoryBean在IOC容器的基础上给Bean的实现加上了一个简单工厂模式和装饰模式。
3. 不止Spring中，包括mybatis和HIibernate框架等大量地方用到了FactoryBean，可见其设计思想的重要性。
##  BeanFactory
BeanFactory是Spring里面最底层的一个接口，提供了最简单的容器的功能，即实例化、配置和管理 Bean。

Spring中实例化容器的几种方式：

> 1.类路径下实例化容器
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
UserFactoryBean userFactoryBean = (UserFactoryBean) context.getBean("userFactoryBean");
2.系统目录下实例化容器
ApplicationContext context = new FileSystemXmlApplicationContext(new String[]{"d:\\beans.xml"});
UserFactoryBean userFactoryBean = (UserFactoryBean) context.getBean("userFactoryBean");
3.注解的方式实例化容器
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Appconfig.class);
UserFactoryBean userFactoryBean = (UserFactoryBean) context.getBean("userFactoryBean");

看一下BeanFactory的方法：
- boolean containsBean(String beanName) 判断工厂中是否包含给定名称的bean定义，若有则返回true
- Object getBean(String) 返回给定名称注册的bean实例。根据bean的配置情况，如果是singleton模式将返回一个共享实例，否则将返回一个新建的实例，如果没有找到指定bean,该方法可能会抛出异常
- Object getBean(String, Class) 返回以给定名称注册的bean实例，并转换为给定class类型
- Class getType(String name) 返回给定名称的bean的Class,如果没有找到指定的bean实例，则排除NoSuchBeanDefinitionException异常
- boolean isSingleton(String) 判断给定名称的bean定义是否为单例模式
- String[] getAliases(String name) 返回给定bean名称的所有别名

##  FactoryBean
FactoryBean也是一个接口，它是一个bean，一个特殊的bean。实现了factorybean的bean，实现了如下3个方法：
- T getObject() throws Exception：实际返回的bean对象
- Class<?> getObjectType()：获取返回bean对象的类型
-  boolean isSingleton()：判断是否是单例

1. 通过重写其中的getObject()方法自定义生成bean逻辑创建一个新的bean，为IOC容器中Bean的实现提供了更加灵活的方式
2. 实现了factorybean的bean会在IOC容器中产生2个bean对象，一个是getObject()方法返回的bean对象，一个是实现了factorybean的bean的这个对象本身
3. 通过@Component，@Service标注了bean名字获取的bean是getObject方法返回的bean；获取实现了factorybean的bean则是通过，& + bean的名字获取

**下面我们看一个简易demo**：

1.编写扫描配置类Appconfig，bean对象User，以及实现了factorybean的bean：UserFactoryBean，重写getObject方法

```
@Configuration
@ComponentScan("com.demo")
@ImportResource("classpath:spring.xml")
public class Appconfig {
}
```

```
public class User {
    private String name;

    private int age;

    private String birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


}
```

```
@Component("userFactoryBean")
public class UserFactoryBean implements FactoryBean {

    private String name;

    private int age;

    private String birthday;

    @Override
    public Object getObject() {
        User user = new User();
        user.setName(name);
        user.setBirthday(birthday);
        user.setAge(age);
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getBirthday() {
        return birthday;
    }
```
2.编写spring.xml配置文件，注入name，age，birthday属性到userFactoryBean

```
  <bean id="userFactoryBean" class="com.demo.UserFactoryBean">
    <property name="name" value="xiaoming"></property>
    <property name="age" value="18"></property>
    <property name="birthday" value="1225"></property>

  </bean>
```
3.编写test测试类

```
public class Test {

    @org.junit.Test
    public void test1(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Appconfig.class);
        // 获取UserFactoryBean对面本身
        UserFactoryBean userFactoryBean = (UserFactoryBean) context.getBean("&userFactoryBean");

        System.out.println("UserFactoryBean------姓名："+userFactoryBean.getName()+";年龄:"+userFactoryBean.getAge()+";生日："+userFactoryBean.getBirthday());
        // 获取实际返回的对象，通过getObject方法返回的对象
        User user = (User) context.getBean("userFactoryBean");
        System.out.println("User------姓名："+user.getName()+";年龄:"+user.getAge()+";生日："+user.getBirthday());
    }
}
```
看一下效果：


![在这里插入图片描述](https://img-blog.csdnimg.cn/20201116151220492.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)

通过上述demo我们可以看到：
1. ==确实生成了2个对象。&userFactoryBean  获取了UserFactoryBean对面本身；userFactoryBean   获取了通过getObject方法返回的对象==
2. ==这里demo里面spring.xml文件一个一个注入属性到UserFactoryBean，若属性特别多，UserFactoryBean内部实现十分复杂，这时又该如何解决呢？？getObjectd的用途之一就是在于此，我们可以把name，age，birthday写了一个字符串在springx.xml注入到UserFactoryBean，然后在getObject方法中拆分字符串给user对象赋值。这里就达到了一个效果：当一个类内部特别复杂的时候，有n多属性，n多依赖关系。想要对外提供一个简单的方式可以配置起来，具体如何封装通过getObjectd方法内部自己实现。==

##  总结
-  BeanFactory是Spring里面最底层的一个接口，提供了最简单的容器的功能，即实例化、配置和管理 Bean。
-  FactoryBean也是一个接口，实现了3个方法，通过重写其中的getObject()方法自定义生成bean逻辑创建一个新的bean，为IOC容器中Bean的实现提供了更加灵活的方式
-  实现了factorybean的bean会在IOC容器中产生2个bean对象，一个是getObject()方法返回的bean对象，一个是实现了factorybean的bean的这个对象本身。通过@Component，@Service标注了bean名字获取的bean是getObject方法返回的bean；获取实现了factorybean的bean则是通过，& + bean的名字获取

> 参考学习博文：
[https://blog.csdn.net/qiesheng/article/details/72875315](https://blog.csdn.net/qiesheng/article/details/72875315)
[https://www.cnblogs.com/aspirant/p/9082858.html](https://www.cnblogs.com/aspirant/p/9082858.html)
[https://blog.csdn.net/wangbiao007/article/details/53183764](https://blog.csdn.net/wangbiao007/article/details/53183764)<br>
> 本文以及demo以及收录至：[https://github.com/taochengbin/javaSourceCode/tree/master/spring](https://github.com/taochengbin/javaSourceCode/tree/master/spring)






