import com.demo.config.SpringConfig;
import com.demo.dao.UserDaoImpl;
import com.demo.service.UserServiceImpl;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDemo {
    @org.junit.Test
    public  void test1() {
        //1. 基于xml获取bean
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        UserServiceImpl userServiceImpl = (UserServiceImpl) classPathXmlApplicationContext.getBean("userService");
        userServiceImpl.testService();

    }

    @Test
    public void test2(){
        //2. 基于注解获取bean
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        UserServiceImpl userServiceImpl = (UserServiceImpl) classPathXmlApplicationContext.getBean("userServiceImpl");
        userServiceImpl.testService();
    }

    @Test
    public void test3(){
        //3. Java-based 基于 java Configuration
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        UserServiceImpl userServiceImpl = (UserServiceImpl) annotationConfigApplicationContext.getBean("userServiceImpl");
        userServiceImpl.testService();
    }

    @Test
    public void test4(){
        //4. Java-based与xml结合使用
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        UserServiceImpl userServiceImpl = (UserServiceImpl) annotationConfigApplicationContext.getBean("userService");
        userServiceImpl.testService();
    }
}
