@[toc]
##  前言
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;谈起spring，Aop当然是其中的重中之重。本文简要讲解Aop理论知识，以及根据demo着重讲解Aspectj的基本用法。spring Aop 的语法这里不作介绍。
##  Aop是什么？
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aop是面向切面编程，它通过动态代理的方式为程序添加统一的功能，集中解决一些公共问题。如日志处理，事务控制等等。
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;传统的开发中，我们的代码都是自上而下OOP编程。而这些过程会产生一些横切性问题，这些横切性的问题和我们的主业务逻辑关系不大，这些横切性问题不会影响到主逻辑实现的，但是会散落到代码的各个部分，难以维护。AOP是处理一些横切性问题，AOP的编程思想就是把这些问题和主业务逻辑分开，达到与主业务逻辑解耦的目的。使代码的重用性和开发效率更高。

**Aop应用场景：**
- 日志记录
- 权限验证
- 效率检查
- 事务管理
##  springAop和AspectJ的关系
1. SpringAop和AspectJ是Aop的不同实现方式
2. Spring通过xml文件的配置实现Aop，功能能实现，但是xml配置实在过于复杂
3. Aspectj支持注解的方式，提供一种比较简便的方式实现Aop
4. Spring中提供了对AspectJ的支持
##  Aop的一些理论概念

> Aspect切面：跨多个类的关注点的模块化
Join point连接点：程序执行期间的一个点，指需要增强的方法
Pointcut切入点：匹配连接点的谓词。连接点的集合，也就是匹配的需要增强的方法的集合
AOP proxy AOP代理: AOP框架为了实现方面契约(通知方法的执行等等)而创建的对象。在Spring框架中，AOP代理是JDK动态代理或CGLIB代理。
Weaving 织入：把代理逻辑加入到目标对象上的过程叫做织入
>

>Spring AOP包括以下类型的通知:
Before advice:在连接点之前运行但不能阻止执行流继续到连接点的通知(除非它抛出异常)。
After returning advice:通知将在连接点正常完成后运行(例如，如果方法返回时没有抛出异常)。
After throwing advice抛出通知后:如果方法通过抛出异常而退出，则将运行通知。
After (finally) advice通知之后(最后):无论连接点以何种方式退出(正常或异常返回)，都要运行的通知。
Around advice环绕通知:围绕连接点(如方法调用)的通知。这是最有力的建议。Around通知可以在方法调用之前和之后执行自定义行为。它还负责选择是继续到连接点，还是通过返回自己的返回值或抛出异常来简化建议的方法的执行。

##  Spring Aop集成AspectJ
###  首先搭建一个简易demo（execution语法）
1.创建AspectjConfig配置类，开启Aop。@ComponentScan指定扫描的包自动注入

```
@Configuration
@ComponentScan("com.demo")
@EnableAspectJAutoProxy(proxyTargetClass=false)
public class AspectjConfig {
}
```
2.声明切面类，@Pointcut指定哪些方法需要进行增强，这里指定UserDao.testExecution()方法。
@Before前置通知，在增强的方法执行前处理一些逻辑
@Around环绕通知，在增强的方法执行前和执行后分别处理一些逻辑
@After后置通知，在增强的方法执行后处理一些逻辑
```
@Component
@Aspect
public class Aspectj {

    // test Execution
    @Pointcut("execution(* com.demo.dao.UserDao.testExecution())")
    public void pointCutExecution(){
        System.out.println("pointcut Execution----");
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

}
```
3.编写dao以及dao实现类

```
public interface UserDao {
    public void testExecution();
}
```

```
@Repository("UserDaoImpl")
public class UserDaoImpl implements UserDao{
    @Override
    public void testExecution() {
        System.out.println("这是dao业务逻辑----");
    }
}
```
4.编写测试类TestDemo

```
public class TestDemo {
    @Test
    public void testExecution(){
        // jdk 动态代理的方式  通过接口，只能强转为UserDao接口，强转为实现类UserDaoImpl会报错
        // 原因：jdk动态代理获取的bean已经继承了proxy，且实现了接口，单继承
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AspectjConfig.class);
        UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("UserDaoImpl");
        userDao.testExecution();
    }

    @Test
    public void testExecution2(){
        // cglib代理
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AspectjConfig.class);
        UserDaoImpl userDaoImpl = (UserDaoImpl) annotationConfigApplicationContext.getBean("UserDaoImpl");
        userDaoImpl.testExecution();
    }
}
```
执行一下测试类的testExecution，我们可以看对dao的方法testExecution进行了增强。前置通知，环绕通知，后置通知。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201102110213708.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
==这里有个知识点注意一下：
1.TestDemo 的testExecution方法是通过JDK动态代理的方式进行增强，此方式容器取得的bean对象指向的接口： UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("UserDaoImpl");
2.TestDemo 的testExecution2方法是通过cglib动态代理的方式进行增强，此方式容器取得的bean对象指向的目标对象，也就是实现类： UserDaoImpl userDaoImpl = (UserDaoImpl) annotationConfigApplicationContext.getBean("UserDaoImpl");==

原因在于：jdk动态代理获取的bean已经继承了proxy，且实现了一个接口，由于Java是单继承，不能强转为实现类。

###  execution，within，args，target，@annotation指定连接点语法以及demo
####  语法
1.    execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)
                throws-pattern?)
execution不止可以指定包，还可以可以精确到方法的返回值，参数个数、修饰符、参数类型等

> modifiers-pattern：方法的可见性，如public，protected；
ret-type-pattern：方法的返回值类型，如int，void等；
declaring-type-pattern：方法所在类的全路径名，如com.spring.Aspect；
name-pattern：方法名类型，如buisinessService()；
param-pattern：方法的参数类型，如java.lang.String；
throws-pattern：方法抛出的异常类型，如java.lang.Exception；
==example:==
@Pointcut("execution(* com.demo.dao.*.*(..))")//匹配com.demo.dao包下的任意接口和类的任意方法
@Pointcut("execution(public * com.demo.dao.*.*(..))")//匹配com.demo.dao包下的任意接口和类的public方法
@Pointcut("execution(public * com.demo.dao.*.*())")//匹配com.demo.dao包下的任意接口和类的public 无方法参数的方法
@Pointcut("execution(* com.demo.dao.*.*(java.lang.String, ..))")//匹配com.demo.dao包下的任意接口和类的第一个参数为String类型的方法
@Pointcut("execution(* com.demo.dao.*.*(java.lang.String))")//匹配com.demo.dao包下的任意接口和类的只有一个参数，且参数为String类型的方法
@Pointcut("execution(* com.demo.dao.*.*(java.lang.String))")//匹配com.demo.dao包下的任意接口和类的只有一个参数，且参数为String类型的方法
@Pointcut("execution(public * *(..))")//匹配任意的public方法
@Pointcut("execution(* te*(..))")//匹配任意的以te开头的方法
@Pointcut("execution(* com.demo.dao.IndexDao.*(..))")//匹配com.demo.dao.IndexDao接口中任意的方法
@Pointcut("execution(* com.demo.dao..*.*(..))")//匹配com.demo.dao包及其子包中任意的方法

2.within
within与execution相比，只能指定到包，接口和类级别。

> @Pointcut("within(com.demo.dao.*)")//匹配com.demo.dao包中的任意方法
@Pointcut("within(com.demo.dao..*)")//匹配com.demo.dao包及其子包中的任意方法

3.args:用于匹配指定参数类型和指定参数数量的方法,与包名和类名无关

>  @Pointcut("args(java.lang.String)")//匹配运行时传递的参数类型为指定类型（这里String）的、且参数个数和顺序匹配
@Pointcut("@args(com.demo.anno.Annotation)")//接受一个参数，并且传递的参数的运行时类型具有@Classified

4.target: 指向接口和子类

> @Pointcut("target(com.demo.dao.IndexDaoImpl)")//目标对象，也就是被代理的对象。限制目标对象为com.demo.dao.IndexDaoImpl类
@Pointcut("this(com.demo.dao.IndexDaoImpl)")//当前对象，也就是代理对象，代理对象时通过代理目标对象的方式获取新的对象，与原值并非一个
@Pointcut("@target(com.demo.anno.Annotation)")//具有@Annotation的目标对象中的任意方法
@Pointcut("@within(com.demo.anno.Annotation)")//等同于@targ

5.@annotation: 对标有@annotation注解的方法进行增强

```
@Pointcut("@annotation(com.demo.anno.Annotation)")//匹配带有com.demo.anno.Annotation注解的方法
```
6.bean

> @Pointcut("bean(dao1)")//名称为dao1的bean上的任意方法
@Pointcut("bean(dao*)")

####  demo
在上面的demo下新增部分代码
1.Aspectj 类：
```
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
```
2.UserDao 和实现类
```
public interface UserDao {
    public void testExecution();

    public void testWithin();

    public void testArgs(String str);

    public void testTarget();

    public void testAnnotation();
}
```

```
@Repository("UserDaoImpl")
public class UserDaoImpl implements UserDao{
    @Override
    public void testExecution() {
        System.out.println("这是dao业务逻辑----");
    }

    @Override
    public void testWithin() {
        System.out.println("testWithin:这是dao业务逻辑----");
    }

    @Override
    public void testArgs(String str) {
        System.out.println("testArgs:这是dao业务逻辑----");
    }

    @Override
    public void testTarget() {
        System.out.println("testTarget:这是dao业务逻辑----");
    }

    @Override
    @UserAnnotation(str = "aaaaa")
    public void testAnnotation() {
        System.out.println("testAnnotation:这是dao业务逻辑----");
    }
}
```
3.新建注解类：
```
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface UserAnnotation {

    String str() default "annotation";
}
```
4.TestDemo测试类

```
public class TestDemo {
    @Test
    public void testExecution(){
        // jdk 动态代理的方式  通过接口，只能强转为UserDao接口，强转为实现类UserDaoImpl会报错
        // 原因：jdk动态代理获取的bean已经继承了proxy，且实现了接口，单继承
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AspectjConfig.class);
        UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("UserDaoImpl");
        userDao.testExecution();
    }

    @Test
    public void testExecution2(){
        // cglib代理
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AspectjConfig.class);
        UserDaoImpl userDaoImpl = (UserDaoImpl) annotationConfigApplicationContext.getBean("UserDaoImpl");
        userDaoImpl.testExecution();
    }

    @Test
    public void testArgs(){
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AspectjConfig.class);
        UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("UserDaoImpl");
        userDao.testArgs("testArgs");
    }

    @Test
    public void testAnnotation(){
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(AspectjConfig.class);
        UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("UserDaoImpl");
        userDao.testAnnotation();
    }
}
```
##  其他知识点
1.Introductions中 @DeclareParents：为目标类手动的添加其他接口功能的实现方法。
用法参考博文：[https://blog.csdn.net/u010502101/article/details/76944753](https://blog.csdn.net/u010502101/article/details/76944753)

官网介绍：

您可以使用@DeclareParents注释进行介绍。此注释用于声明匹配类型有一个新的父类(因此得名)。例如，给定一个名为UsageTracked的接口和该接口的一个名为DefaultUsageTracked的实现，下面的方面声明所有服务接口的实现者也都实现了UsageTracked接口(例如通过JMX公开统计数据):

```
@Aspect
public class UsageTracking {

    @DeclareParents(value="com.xzy.myapp.service.*+", defaultImpl=DefaultUsageTracked.class)
    public static UsageTracked mixin;

    @Before("com.xyz.myapp.CommonPointcuts.businessService() && this(usageTracked)")
    public void recordUsage(UsageTracked usageTracked) {
        usageTracked.incrementUseCount();
    }

}
```

```
UsageTracked usageTracked = (UsageTracked) context.getBean("myService");
```

 2.Aspect Instantiation Models中  @AspectJ("perthis(com.demo.xxx)")：perthis指定包名xxx下的所有类添加同一个切面。默认AspectJ切面是单例的，Aspect Instantiation Models不是单例
##  总结
1.cglib是通过继承来操作子类的字节码生成代理类
2.JDK是通过接口,然后利用java反射完成对类的动态创建
3.可以通过@EnableAspectJAutoProxy注解的proxyTargetClass属性指定创建方式,false为jdk，true为cglib代理


本文demo以及博文已收录至：[https://github.com/taochengbin/javaSourceCode/tree/master/spring](https://github.com/taochengbin/javaSourceCode/tree/master/spring)



