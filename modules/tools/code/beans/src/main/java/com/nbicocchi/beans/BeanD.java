package com.nbicocchi.beans;

import org.springframework.stereotype.Component;

@Component
public class BeanD {
    BeanB beanB1;
    BeanB beanB2;
    BeanC beanC1;
    BeanC beanC2;

    public BeanD(BeanB beanB1, BeanB beanB2, BeanC beanC1, BeanC beanC2) {
        this.beanB1 = beanB1;
        this.beanB2 = beanB2;
        this.beanC1 = beanC1;
        this.beanC2 = beanC2;
    }
}
