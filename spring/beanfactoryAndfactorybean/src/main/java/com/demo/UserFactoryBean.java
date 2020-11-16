package com.demo;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

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
}
