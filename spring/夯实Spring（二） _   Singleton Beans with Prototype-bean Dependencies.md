##  官网介绍
<font color=red>Singleton Beans with Prototype-bean Dependencies：Spring中单例bean引入Prototype原型bean导致Prototype bean失效问题，此时的Prototype bean始终是同一个bean<font/>

首先看一看官方文档：[https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html)


![在这里插入图片描述](https://img-blog.csdnimg.cn/2020102315211171.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
##  翻译
翻译如下：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;当您使用带有原型bean依赖项的单例范围bean时，请注意依赖项是在实例化时解析的。因此，如果您依赖地将一个原型作用域的bean注入到一个单例作用域的bean中，那么一个新的原型bean将被实例化，然后依赖地注入到单例bean中。原型实例是提供给单例作用域bean的唯一实例。


==什么意思呢？==
==就是当一个Singleton 单例的bean，beanA，依赖一个 Prototype原型的bean，beanB。此时这个Prototype beanB就失去意义，始终是同一个bean，每次从容器获取的都是同一个beanB对象==

##  原因
看一看官网：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201023153227657.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
翻译如下：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在大多数应用程序场景中，容器中的大多数bean都是单例的。当一个单例bean需要与另一个单例bean协作时，或者一个非单例bean需要与另一个非单例bean协作时，通常通过将一个bean定义为另一个bean的属性来处理依赖关系。当bean的生命周期不同时，问题就出现了。假设单例bean A需要使用非单例(原型)bean B，可能在对A的每次方法调用上都是如此，容器只创建一次单例bean A，因此只获得一次设置属性的机会。容器不能在每次需要bean B的时候都向bean A提供一个新的实例。

==简单来说：因为spring容器只创建一次单例bean beanA，同时把beanB依赖进去。之后再没有机会重新依赖注入beanB。所以从头到尾原型beanB就只是同一个bean。==

##  如何解决这个问题
官网也有介绍，上述图下面的代码就是：
###  解决方式一
1.实现ApplicationContextAware 接口
2.实现setApplicationContext方法
3.applicationContext 上下文getbean

此时得到的bean就永远不是同一个。
==这种方式侵入性太强，需要依赖spring的接口==

```
public class CommandManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Object process(Map commandState) {
        // grab a new instance of the appropriate Command
        Command command = createCommand();
        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
        return command.execute();
    }

    protected Command createCommand() {
        // notice the Spring API dependency!
        return this.applicationContext.getBean("command", Command.class);
    }

    public void setApplicationContext(
            ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```
###  解决方式二
跟着官网往下面看：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201023154053359.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
==xml方式：添加lookup-method标签==

```
<!-- a stateful bean deployed as a prototype (non-singleton) -->
<bean id="myCommand" class="fiona.apple.AsyncCommand" scope="prototype">
    <!-- inject dependencies here as required -->
</bean>

<!-- commandProcessor uses statefulCommandHelper -->
<bean id="commandManager" class="fiona.apple.CommandManager">
    <lookup-method name="createCommand" bean="myCommand"/>
</bean>
```
==注解方式：抽象方法上面添加@Lookup注解，并设置依赖的原型bean==

```
public abstract class CommandManager {

    public Object process(Object commandState) {
        Command command = createCommand();
        command.setState(commandState);
        return command.execute();
    }

    @Lookup("myCommand")
    protected abstract Command createCommand();
}
```





