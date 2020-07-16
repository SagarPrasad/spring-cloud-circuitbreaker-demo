package org.spring.samples.demo;

import javax.annotation.PostConstruct;

import lombok.extern.log4j.Log4j2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@Log4j2
public class SpringExamplesDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringExamplesDemo.class, args);
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }


    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        // to do some post processor events
        log.info("I AM THE LAST STEP - APPLICATION IS READY TO USE ");
    }

    @PostConstruct
    public void init() {
        // to capture application started
        log.info("AFTER ALL THE BEAN INITIALIZATION");
        // start your monitoring in here
    }

}
