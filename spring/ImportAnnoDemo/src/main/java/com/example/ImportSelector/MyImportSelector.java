package com.example.ImportSelector;

import com.example.daoImpl.UserDaoImpl2;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{UserDaoImpl2.class.getName()};
    }
}
