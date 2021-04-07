import com.demo.AppConfig;
import com.demo.UserDaoImpl;
import com.demo.UserDao;
import com.demo.beaninit.UserDaoImpl4;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test1 {

	public static void main(String[] args) {
//		System.out.println("aaaaa水水水水水水水水水水水水水水水水水水水============");
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
//		UserDaoImpl dao = (UserDaoImpl) context.getBean("dao");
//		dao.test();


		// DefaultListableBeanFactory
		// 实例化工厂，准备reader和scanner
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

		// 把AppConfig类注册为一个BeanDefinition，然后放到DefaultListableBeanFactory的beanDefinitionMap里面
		context.register(AppConfig.class);

		//准备好bean工厂，实例化对象
		context.refresh();

		UserDaoImpl dao =  context.getBean(UserDaoImpl.class);
		UserDaoImpl da1 =  context.getBean(UserDaoImpl.class);
		System.out.println(dao.hashCode()+"-----------------"+da1.hashCode());
		dao.test();
	}
	@Test
	public void test(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		UserDaoImpl dao = (UserDaoImpl) context.getBean("dao");
		dao.test();
	}

	@org.junit.Test
	public void test1(){
		// DefaultListableBeanFactory
		// 实例化工厂，准备reader和scanner
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

		// 把AppConfig类注册为一个BeanDefinition，然后放到DefaultListableBeanFactory的beanDefinitionMap里面
		context.register(AppConfig.class);

		// 添加自定义后置处理器
		//context.addBeanFactoryPostProcessor(new MyBeanFactoryProcessor());

		// 准备好bean工厂，实例化对象
		context.refresh();

		UserDaoImpl dao = (UserDaoImpl) context.getBean("dao");
		UserDaoImpl da1 = (UserDaoImpl) context.getBean("dao");
		System.out.println(dao.hashCode()+"-----------------"+da1.hashCode());
		dao.test();
	}


	@org.junit.Test
	public void testImport(){
		// DefaultListableBeanFactory
		// 实例化工厂，准备reader和scanner
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

		// 把AppConfig类注册为一个BeanDefinition，然后放到DefaultListableBeanFactory的beanDefinitionMap里面
		context.register(AppConfig.class);

		// 准备好bean工厂，实例化对象
		context.refresh();

//		UserDaoImpl2 bean = context.getBean(UserDaoImpl2.class);
//		System.out.println(bean.getClass().getName());
//		bean.test();

//		UserDaoImpl2 bean2 = (UserDaoImpl2) context.getBean("com.demo.importdemo.UserDaoImpl2");
//		bean2.test();

		UserDao dao = (UserDao) context.getBean("userDaoImpl");
		dao.test();
	}


	@org.junit.Test
	public void testFullAndLite(){
		// DefaultListableBeanFactory
		// 实例化工厂，准备reader和scanner
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

		// 把AppConfig类注册为一个BeanDefinition，然后放到DefaultListableBeanFactory的beanDefinitionMap里面
		context.register(AppConfig.class);

		// 准备好bean工厂，实例化对象
		context.refresh();


		// 违反了单例，所有是cglib
		AppConfig appconfig = (AppConfig) context.getBean("appConfig");

		System.out.println(appconfig);
	}

	@org.junit.Test
	public void testBeanInit(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(AppConfig.class);
		context.refresh();


		// 违反了单例，所有是cglib
		UserDaoImpl4 userDaoImpl4 = (UserDaoImpl4) context.getBean("index");
		userDaoImpl4.test();
	}

}
