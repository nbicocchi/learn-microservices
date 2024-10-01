package com.baeldung.ls.config;

import com.baeldung.ls.persistence.repository.IProjectRepository;
import com.baeldung.ls.persistence.repository.impl.ProjectRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baeldung.ls.persistence.model.BeanA;
import com.baeldung.ls.persistence.model.BeanB;
import com.baeldung.ls.persistence.model.BeanC;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    public BeanA beanA() {
        return new BeanA();
    }

    @Bean(initMethod = "initialize")
    public BeanB beanB() {
        return new BeanB();
    }

    @Bean(destroyMethod = "destroy")
    public BeanC beanC() {
        return new BeanC();
    }

    @Bean
    @Scope("prototype")
    public IProjectRepository singletonBean() {
        return new ProjectRepositoryImpl();
    }
}
