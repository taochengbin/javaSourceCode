package com.example.ImportNormalClass;

import com.example.daoImpl.UserDaoImpl1;
import com.example.daoImpl.UserDaoImpl2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({UserDaoImpl1.class, UserDaoImpl2.class})
public class AppConfig {
}
