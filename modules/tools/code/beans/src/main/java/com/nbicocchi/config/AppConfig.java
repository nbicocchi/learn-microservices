package com.nbicocchi.config;

import com.nbicocchi.beans.BeanB;
import com.nbicocchi.beans.BeanC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {
    @Bean(initMethod="initialize", destroyMethod="destroy")
    @Scope("singleton")
    public BeanB beanB() {
        return new BeanB();
    }

    @Bean(initMethod="initialize", destroyMethod="destroy")
    @Scope("prototype")
    public BeanC beanC() {
        return new BeanC();
    }
}
