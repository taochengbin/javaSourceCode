@[toc]
##  前言
本文阐述了如何搭建spring源码阅读环境，为后续源码学习做准备。
##  安装gradle
1.选择需要的版本，本文6.7。从官网下载gradle：[https://gradle.org/releases/](https://gradle.org/releases/)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113103911790.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
2.解压下载的压缩包，并且配置gradle环境变量
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020111310410581.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
3.新增系统变量GRADLE_HOME，这里配置为解压gradle的路径
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113104146731.png#pic_center)
编辑path变量，新增gradle的变量
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113104304183.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
4.检查是否安装完成，cmd命令行输入gradle -v

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113104503254.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
##  idea2020导入Spring源码
1.选择需要的版本，本文采用的5.3.0
github下载spring源码：[https://github.com/spring-projects/spring-framework/tree/v5.3.0](https://github.com/spring-projects/spring-framework/tree/v5.3.0)
码云下载地址：[https://gitee.com/mirrors/Spring-Framework](https://gitee.com/mirrors/Spring-Framework)
2.idea导入spring源码
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020111310515043.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113105230950.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113105245639.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
3.idea配置gradle
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113105530440.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
4.解决gradle编译下载依赖慢的问题（编译十分缓慢，请耐心等待~~）
在gradle安装目录新建init.gradle文件，文件加入阿里云镜像配置，重新编译

```
allprojects{
    repositories {
        maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://maven.aliyun.com/repository/jcenter'}
		maven { url 'https://maven.aliyun.com/repository/central'}
		maven { url 'https://maven.aliyun.com/repository/public'}
		maven { url 'https://maven.aliyun.com/repository/spring'}
		maven { url 'https://maven.aliyun.com/repository/spring-plugin'}
		maven { url 'https://maven.aliyun.com/repository/grails-core'}
		maven { url 'https://maven.aliyun.com/nexus/content/repositories/gradle-plugin'}
    }
}
```
5.可能会出现的问题
org.mockito，单元测试的包会下载不了。更改项目根目录下build.gradle文件，mockito的版本号，版本降低一点。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113110134101.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
##  新建测试module
1.新建测试module tcb-test，然后等待idea自动编译完成
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113174925500.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113174943743.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
2.在tcb-test  module的build.gradle文件新增配置

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113175011848.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)
3.编写测试类AppConfig,UserDaoImpl,Test1
AppConfig

```
@Configuration
@ComponentScan("com.demo")
public class AppConfig {
}
```
UserDaoImpl

```
@Component("dao")
public class UserDaoImpl {
	public void test(){
		System.out.println("this is tets asdasd a你好啊啊啊啊啊啊啊啊啊啊啊啊啊=========");
	}
}
```
Test1

```
public class Test1 {

	public static void main(String[] args) {
		System.out.println("aaaaa水水水水水水水水水水水水水水水水水水水============");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		UserDaoImpl dao = (UserDaoImpl) context.getBean("dao");
		dao.test();
	}
	}
```
4.运行一下
这里是乱码了，但是可以看出是正常获取了bean
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201113175347123.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDI1ODAz,size_16,color_FFFFFF,t_70#pic_center)












