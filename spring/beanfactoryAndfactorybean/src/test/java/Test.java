import com.demo.Appconfig;
import com.demo.User;
import com.demo.UserFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
