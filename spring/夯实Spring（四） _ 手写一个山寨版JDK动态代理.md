@[toc]
##  前言
从Spring Aop学习到有2种实现方式对类的代理生成从而实现方法的增强：
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.JDK动态代理：通过接口,然后利用java反射完成对类的动态创建
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.Cglib动态代理：通过继承来操作子类的字节码生成代理类
那么如何理解代理？？代理有几种方式？？以及Java是如何实现JDK动态代理的呢？？
本文将着重介绍这块知识点。

##  什么是代理？
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;代理，生活当中处处都可见代理。比如旅行社，小型航空公司代理机票的贩卖；房屋产权人请中介代理房屋的出租，贩卖等等。其中就涉及几个名词，代理对象，目标对象。旅行社代理后的机票就是代理对象，机票是目标对象。旅行社对机票涨价就是对机票的价格增强，也就是对目标对象某方面的增强。这里体现为价格。

##  代理的几种方式
###  静态代理
静态代理有2种方式：
&nbsp;&nbsp;&nbsp;&nbsp;    1. 继承：一个类通过继承另外一个类，从而重写目标方法，达到对目标方法的增强
&nbsp;&nbsp;&nbsp;&nbsp;    2. 聚合：目标对象和代理对象实现同一个接口，代理对象中包含目标对象。在代理对象实现接口的目标方法中调用目标对象同一个接口下的实现方法，同时在方法中写自身的逻辑，达到对方法的增强
形如：
$Proxy代理类包含目标对象UserDao target，实现同一个接口UserDao下的方法testProxy，在方法内打印"Method is enhanced"对方法进行增强，调用testProxy执行原来的功能。
```
 package com.google;
 import com.demo.dao.UserDao;
 public class $Proxy implements UserDao{
 	private UserDao target;
 	public $Proxy (UserDao target){
 		this.target =target;
 	}
 	@Override
 	public void testProxy() {
 		System.out.println("Method is enhanced");
 		target.testProxy();
 	}
 }
```

缺点：
&nbsp;&nbsp;&nbsp;&nbsp;    当一个方法需要被增强多次的时候，继承可以通过链式继承，A继承B，B继承C，重写2次C中方法，从而达到对C中方法增强2次。而聚合则是通过目标对象和代理对象的切换，上一次的代理对象成为下一次代理的目标对象，A代理B，A中包含B。代理完成后，C再代理已经代理过B的A，从而达到代理多次。
&nbsp;&nbsp;&nbsp;&nbsp;  ==由此可看出，无论哪种方式，随着方法增强的次数增大，类都在不停的在增长。最终会导致类爆炸。项目中类太多了，根本无法明确类的作用。因此，动态代理就因此衍生出来==

###  动态代理
&nbsp;&nbsp;&nbsp;&nbsp;    1.JDK动态代理：通过接口,然后利用java反射完成对类的动态创建
&nbsp;&nbsp;&nbsp;&nbsp;    2.Cglib动态代理：通过继承来操作子类的字节码生成代理类
##  自己实现一个JDK动态代理
1.编写UserDao和实现类UserDaoImpl

```
public interface UserDao {
    public void testProxy();
}
```
UserDaoImpl 逻辑打印字符串  "-----这是dao业务逻辑-----"
```
public class UserDaoImpl implements UserDao{
    @Override
    public void testProxy() {
        System.out.println("-----这是dao业务逻辑-----");
    }
}

```
2.编写util类生成代理类

代理类实现步骤：
1. 生成一个代理类的Java文件
  1.1 拼接一个代理类内容的字符串
	  形如：
	  
		```
		package com.google;
		import com.demo.dao.UserDao;
		public class $Proxy implements UserDao{
			private UserDao target;
			public $Proxy (UserDao target){
				this.target =target;
			}
			public void testProxy() {
				System.out.println("Method is enhanced");
				target.testProxy();
			}
		}
		```

	  1.2 对目标对象进行方法的增强，打印Method is enhanced。拼接字符串
	  1.3 写一个$Proxy.java文件 —— 代理对象的java文件
2. 编译成字节码文件
3. 通过反射生成这个代理类

```
public class ProxyUtils {
    public static Object newInstance(Object target){
        // 1.生成代理类的java文件
        // 1.1拼接一个代理类内容的字符串
        Object proxy=null;
        Class targetInf = target.getClass().getInterfaces()[0];
        Method methods[] =targetInf.getDeclaredMethods();
        String line="\n";
        String tab ="\t";
        String infName = targetInf.getSimpleName();
        String content ="";
        String packageContent = "package com.google;"+line;
        String importContent = "import "+targetInf.getName()+";"+line;
        String clazzFirstLineContent = "public class $Proxy implements "+infName+"{"+line;
        String filedContent  =tab+"private "+infName+" target;"+line;
        String constructorContent =tab+"public $Proxy ("+infName+" target){" +line
            +tab+tab+"this.target =target;"
            +line+tab+"}"+line;
        String methodContent = "";
        for (Method method : methods) {
            String returnTypeName = method.getReturnType().getSimpleName();
            String methodName =method.getName();
            // Sting.class String.class
            Class args[] = method.getParameterTypes();
            String argsContent = "";
            String paramsContent="";
            int flag =0;
            for (Class arg : args) {
                String temp = arg.getSimpleName();
                //String
                //String p0,Sting p1,
                argsContent+=temp+" p"+flag+",";
                paramsContent+="p"+flag+",";
                flag++;
            }
            if (argsContent.length()>0){
                argsContent=argsContent.substring(0,argsContent.lastIndexOf(",")-1);
                paramsContent=paramsContent.substring(0,paramsContent.lastIndexOf(",")-1);
            }

            // 1.2对目标对象进行方法的增强，打印Method is enhanced。拼接字符串
            methodContent+=tab+"public "+returnTypeName+" "+methodName+"("+argsContent+") {"+line
                +tab+tab+"System.out.println(\"Method is enhanced\");"+line
                +tab+tab+"target."+methodName+"("+paramsContent+");"+line
                +tab+"}"+line;

        }

        content=packageContent+importContent+clazzFirstLineContent+filedContent+constructorContent+methodContent+"}";

        //1.3 写一个$Proxy.java文件 —— 代理对象的java文件
        File file =new File("d:\\com\\google\\$Proxy.java");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
            
            // 2.调用java 的编译器编译成class文件
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
            Iterable units = fileMgr.getJavaFileObjects(file);
            JavaCompiler.CompilationTask t = compiler.getTask(null, fileMgr, null, null, null, units);
            t.call();
            fileMgr.close();

            //3. 通过反射生成代理类
            URL[] urls = new URL[]{new URL("file:D:\\\\")};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            Class clazz = urlClassLoader.loadClass("com.google.$Proxy");
            Constructor constructor = clazz.getConstructor(targetInf);
            proxy = constructor.newInstance(target);
            //clazz.newInstance();
            //Class.forName()
        }catch (Exception e){
            e.printStackTrace();
        }

        return proxy;
    }
}
```
3.生成测试类测试

```
public class TestProxy {

    @Test
    public void test(){
        UserDao proxy = (UserDao) ProxyUtils.newInstance(new UserDaoImpl());
        proxy.testProxy();

    }
}
```
4.结果

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105102441216.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
由图可知成功，在dao的业务逻辑前面打印了Method is enhanced，进行了方法的增强。

##  看一看JDK动态代理的使用方式
1.新建TestJDKInvocationHandler类，处理器。
实现InvocationHandler接口，重写invoke方法，在方法逻辑里面进行方法增强逻辑

```
public class TestJDKInvocationHandler implements InvocationHandler {

    Object target;
    public TestJDKInvocationHandler(Object target){
        this.target=target;
    }
    /**
     *
     * @param proxy 代理对象
     * @param method 目标对象的方法
     * @param args    目标方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("JDK 方法增强");
        return method.invoke(target,args);
    }

}
```
2.新建测试类方法

```
    @Test
    public void testJDkProxy(){
        UserDao jdkproxy = (UserDao) Proxy.newProxyInstance(Test.class.getClassLoader(),
            new Class[]{UserDao.class},new TestJDKInvocationHandler(new UserDaoImpl()));
        try {
            jdkproxy.testProxy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
测试一下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201105113442281.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
我们可以看到进行了方法增强。

>本文以及demo已收录至：[https://github.com/taochengbin/javaSourceCode/tree/master/spring](https://github.com/taochengbin/javaSourceCode/tree/master/spring)
