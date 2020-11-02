import com.demo.config.AspectjConfig;
import com.demo.dao.UserDao;
import com.demo.dao.UserDaoImpl;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
