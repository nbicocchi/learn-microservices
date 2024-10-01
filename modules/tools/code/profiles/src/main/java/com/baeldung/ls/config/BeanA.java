package com.baeldung.ls.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanA {
    private static final Logger LOG = LoggerFactory.getLogger(BeanA.class);
    private String profile;

    public BeanA(String profile) {
        this.profile = profile;
    }

    @PostConstruct
    public void postConstruct() {
        LOG.info(getClass().getName() + " activated with profile " + profile);
    }
}
