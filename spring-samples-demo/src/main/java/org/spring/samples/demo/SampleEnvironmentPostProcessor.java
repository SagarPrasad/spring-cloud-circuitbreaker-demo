/*
 * Copyright (c) 2016 JCPenney Co. All rights reserved.
 *
 */

package org.spring.samples.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author sprasad9
 */
@Order()
public class SampleEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // to override MDC -> logger param like sleuth
        // environment.setActiveProfiles("local");
        System.out.println(environment.getSystemProperties());
        System.out.println("I am the end loader");
    }
}
