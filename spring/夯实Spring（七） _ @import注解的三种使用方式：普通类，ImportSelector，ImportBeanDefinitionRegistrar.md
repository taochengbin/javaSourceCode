@[toc]
##  前言
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;最近在阅读spring源码过程中，发现源码中有对@import注解的处理，且这个注解相关知识点在springboot，springcloud以及mybatis框架中或多或少都有用到，由此可见其的重要性。然而本人对@import相关知识点也了解得不多，遂作此篇文章对这个知识点的整理与学习。
##  @import简述
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@import 顾名思义，导入。即：手动的将一个类放入spring容器中。主要用于导入@Configuration 类，ImportSelector和ImportBeanDefinitionRegistrar接口的实现类 ，也可以导入普通类。下面分别通过demo看一看他的各种使用方式。
- 导入普通类
- 导入实现了ImportSelector接口的类
- 导入实现了ImportBeanDefinitionRegistrar接口的类
##  导入普通类
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@import对于普通类就是单纯的将一个类放入容器中。看一下demo：
1.新建dao以及3个实现类

```
public interface UserDao {
    public void test();
}

public class UserDaoImpl1 implements UserDao{

    @Override
    public void test(){
        System.out.println("bean：UserDaoImpl1");
    }
}

public class UserDaoImpl2 implements UserDao {

    @Override
    public void test() {
        System.out.println("bean：UserDaoImpl2");
    }
}

public class UserDaoImpl3 implements UserDao{

    @Override
    public void test(){
        System.out.println("bean：UserDaoImpl3");
    }
}
```
2.新建导入普通类的配置类

```
@Configuration
@Import({UserDaoImpl1.class, UserDaoImpl2.class})
public class AppConfig {
}
```
3.测试类

```
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
}
```
运行一下：
我们可以看到：
      ==1.在没有加@ComponentScan，@Component注解的情况下，UserDaoImpl1和UserDaoImpl2都成功的放入到了容器中。==
      ==2.@imort导入普通类只需要在@import注解后面添加需要导入的class，它支持导入多个==
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210309152437617.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)
##  导入实现了ImportSelector接口的类
ImportSelector通过selectImports方法返回一个或者多个类名，把它变成beanDefinition，动态添加beanDefinition
###  基本使用
首先看一看demo：
1.新建MyImportSelector类实现ImportSelector接口

```
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{UserDaoImpl2.class.getName()};
    }
}
```
2.新建配置类AppSelectorConfig 

```
@Configuration
@Import({MyImportSelector.class, UserDaoImpl1.class})
public class AppSelectorConfig {
}
```
3.测试类

```
    @Test
    public void testImportSelector1() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppSelectorConfig.class);
        context.refresh();

        UserDaoImpl2 dao2 = context.getBean(UserDaoImpl2.class);
        dao2.test();

    }
```
4.结果
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210309153113767.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)
我们也可以看到ImportSelector成功导入了UserDaoImpl2到容器中。它的使用方式如下：
- 实现ImportSelector接口，重写selectImports方法，返回需要导入的类的名字的字符串数组，数组指可以导入多个类
- 配置类上面的@import注解添加实现ImportSelector接口的类

这样一看，其实感觉ImportSelector的作用以及重要性并没有体现出来，感觉就一般般~~~
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210309153536880.gif#pic_center)
###  升华一：增加动态代理
下面升华一下，改造一下demo：

1.UserDaoImpl2 新实现一个接口BeanPostProcessor，重写它的postProcessBeforeInitialization方法。BeanPostProcessor接口可以插手bean的实例化过程，实例化过后，它作用于在bean放入容器之前。这里重写的逻辑为：如果发现容器有UserDaoImpl1，则把它改为动态代理生成。
```
public class UserDaoImpl2 implements BeanPostProcessor,UserDao {

    @Override
    public void test() {
        System.out.println("bean：UserDaoImpl2");
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("com.example.daoImpl.UserDaoImpl1")){
            bean = Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{UserDao.class},new MyInvocationHandler(bean));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
```
2.配置类@import注解导入UserDaoImpl1

```
@Configuration
@Import({MyImportSelector.class, UserDaoImpl1.class})
public class AppSelectorConfig {
}
```
3.编写代理类，增强原类。（类似于AOP，这里的增强只是打印了一句话：proxy-------------）。它用在BeanPostProcessor接口的postProcessBeforeInitialization方法。



```
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
```
4.测试类

```
    @Test
    public void testImportSelector2() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppSelectorConfig.class);
        context.refresh();

        UserDao dao = (UserDao) context.getBean("com.example.daoImpl.UserDaoImpl1");
        dao.test();

    }
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210309155820316.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)

debug一下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210309155142306.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)
根据上面的demo以及演示结果，我们升华的代码逻辑为：
- 1.通过ImportSelector导入UserDaoImpl2，@import导入普通类的方式导入UserDaoImpl1
- 2.UserDaoImpl2实现BeanPostProcessor接口，在bean实例化的时候，对UserDaoImpl1改造为动态代理生成的类，增强了逻辑添加打印 "proxy-------------"的功能。
- 3.代理类加入容器

验证逻辑：
- 1.proxy-------------在控制台打印了出来，可以看到对方法进行了增强。也执行了userDaoImpl2和userDaoImpl1的test方法。可见容器中有userDaoImpl1和userDaoImpl2
- 2.debug中发现不能通过类型获取userDaoImpl1，会报错nosuchbean，可见容器中userDaoImpl1为代理类

###  升华二：构造自定义注解
下面我们再改造一下代码：

1.新增自定义注解MyImport
```
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MyImportSelector.class, UserDaoImpl1.class})
public @interface MyImport {
}

```
2.配置类使用自定义注解，去掉之前的@import

```
@Configuration
@MyImport
public class AppSelectorConfig {
}
```
3.测试一下
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210309160417622.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)
由图可见，结果还是一致的。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210309160451687.jpg#pic_center)
##  导入实现了ImportBeanDefinitionRegistrar接口的类
ImportBeanDefinitionRegistrar的作用在于导入自定义bean

还是一样首先看一看demo：
1.新建MyImportBeanDefinitionRegistrar类实现ImportBeanDefinitionRegistrar 接口

```
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

```
2.新建配置类AppImportRegistrarConfig 

```
@Configuration
@Import(MyImportBeanDefinitionRegistrar.class)
public class AppImportRegistrarConfig {
}
```
3.测试类

```
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
```
4.测试结果
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021030916073991.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)
根据demo和演示结果，我们可以看到ImportBeanDefinitionRegistrar的使用方式
- 实现ImportBeanDefinitionRegistrar接口，重写registerBeanDefinitions方法。新建自定义的bean，之后放入容器中
- 配置类@import注解后添加实现ImportBeanDefinitionRegistrar接口的类

演示结果：
- 打印了容器中所有的bean名字
- 打印了在ImportBeanDefinitionRegistrar中自定义的bean：dao3333

==可见ImportBeanDefinitionRegistrar的作用在于导入自定义bean==
==这里需要说明一下：==
==ImportBeanDefinitionRegistrar的2个实现方法registerBeanDefinitions，带有BeanNameGenerator名字生成器的方法优先级要高一些，若2个方法都存在，则下图注释的方法会执行。==

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210309163155804.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)


##  源码分析
这里只作简要分析，详情可翻阅之前写的博客：[Spring源码学习（三） | ConfigurationClassPostProcessor解析配置类](https://blog.csdn.net/qq_38425803/article/details/113842399)

<font color='whitgreen'>配置类是否有@import注解，有的话核心逻辑如下：</font>
- <font color='whitgreen'>若没有@Import直接返回</font>
- <font color='whitgreen'>处理ImportSelector接口的情况添加一个BeanDefinition</font>
- <font color='whitgreen'>处理ImportBeanDefinitionRegistrar接口的情况添加一个BeanDefinition</font>
- <font color='whitgreen'>处理默认@import情况添加一个BeanDefinition</font>


```
	private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
			Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
			boolean checkForCircularImports) {

		// 没有import注解，则返回
		if (importCandidates.isEmpty()) {
			return;
		}

		if (checkForCircularImports && isChainedImportOnStack(configClass)) {
			this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
		}
		else {
			// 如果有import注解，@import注解可以放入参数，参数为普通类，ImportSelector.class,ImportBeanDefinitionRegistrar.class
			// ImportBeanDefinitionRegistrar这个类可以动态往BeanDefinitionMap中添加BeanDefinition。暴露了spring的BeanDefinitionMap，可以往里面动态添加
			this.importStack.push(configClass);
			try {
				for (SourceClass candidate : importCandidates) {
					if (candidate.isAssignable(ImportSelector.class)) {
						// Candidate class is an ImportSelector -> delegate to it to determine imports
						Class<?> candidateClass = candidate.loadClass();
						ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
								this.environment, this.resourceLoader, this.registry);
						Predicate<String> selectorFilter = selector.getExclusionFilter();
						if (selectorFilter != null) {
							exclusionFilter = exclusionFilter.or(selectorFilter);
						}
						if (selector instanceof DeferredImportSelector) {
							this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
						}
						else {
							String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
							Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
							processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
						}
					}
					// ImportBeanDefinitionRegistrar动态添加BeanDefinition
					else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
						// Candidate class is an ImportBeanDefinitionRegistrar ->
						// delegate to it to register additional bean definitions
						Class<?> candidateClass = candidate.loadClass();
						ImportBeanDefinitionRegistrar registrar =
								ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
										this.environment, this.resourceLoader, this.registry);
						configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
					}
					else {
						// Candidate class not an ImportSelector or ImportBeanDefinitionRegistrar ->
						// process it as an @Configuration class
						this.importStack.registerImport(
								currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
						processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter);
					}
				}
			}
			catch (BeanDefinitionStoreException ex) {
				throw ex;
			}
			catch (Throwable ex) {
				throw new BeanDefinitionStoreException(
						"Failed to process import candidates for configuration class [" +
						configClass.getMetadata().getClassName() + "]", ex);
			}
			finally {
				this.importStack.pop();
			}
		}
	}
```
#####  ImportSelector
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021022517271879.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)
看一看源码，我们可以看到ImportSelector的方式添加BeanDefinition逻辑如下：
- 通过反射new出实现了ImportSelector接口的类的对象
- 如果这个对象属于DeferredImportSelector，则后续会使用Order接口来进行排序
- 如果这个对象不属于DeferredImportSelector，则调用selectImports方法返回实际需要添加的类的名字数组
- 转换返回的名字数组为SourceClass
- 递归调用processImports，处理新添加的类是否含有@import注解的情况

处理完注意一点：
返回上一层processConfigurationClass方法中，我们可以看到最后把处理完的类放入configurationClasses，此时还未放入BeanDefinitionMap
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210225173757997.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)


#####  ImportBeanDefinitionRegistrar
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210225173951318.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20210225174003822.png)
看一看源码，我们可以看到逻辑如下：
- 通过反射实例化ImportBeanDefinitionRegistrar
- 把ImportBeanDefinitionRegistrar放入importBeanDefinitionRegistrars

注意一下，此时还未放入新添加的bean的BeanDefinition到BeanDefinitionMap

#####  @import普通类
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210225174235712.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210225174330203.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70)
由源码可以看到，这里又调用的之前处理配置类的入口，进去后又处理各种注解。最终把解析的类放入configurationClasses，此时依旧未注册到BeanDefinitionMap

##  总结
- @import导入普通类，就是把一个类手动放入容器中
- ImportSelector通过selectImports方法返回一个或者多个类名，把它们转换为bean放入容器中
- ImportBeanDefinitionRegistrar的作用在于导入自定义bean（自己构造bean的scope，懒加载等等之类）
- 三种方式可以混合使用不影响

> 上一篇：[夯实Spring（六） | BeanFactory 与 FactoryBean](https://blog.csdn.net/qq_38425803/article/details/109720718)
> <br>
> 本文以及demo已收录至GitHub：https://github.com/taochengbin/javaSourceCode/tree/master/spring

