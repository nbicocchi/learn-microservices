package com.nbicocchi.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanB {
    private static final Logger log = LoggerFactory.getLogger(BeanB.class);

    public void initialize() {
        log.info("Custom initialize() is called.");
    }

    public void destroy() {
        log.info("Custom destroy() is called.");
    }
}