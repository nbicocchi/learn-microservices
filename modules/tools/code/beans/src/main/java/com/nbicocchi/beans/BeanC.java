package com.nbicocchi.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanC {
    private static final Logger log = LoggerFactory.getLogger(BeanC.class);

    public void initialize() {
        log.info("Custom initialize() is called.");
    }

    public void destroy() {
        log.info("Custom destroy() is called.");
    }
}