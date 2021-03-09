package com.example.ImportSelector;

import com.example.daoImpl.UserDaoImpl1;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MyImportSelector.class, UserDaoImpl1.class})
public @interface MyImport {
}
