package org.spring.samples.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Log4j2
public class CustomHttpClient {

    // or this should have individual client
    private final WebClient rest;

    public CustomHttpClient(WebClient.Builder builder) {
        this.rest = builder.baseUrl("http://localhost:8080").build();
    }

    private static void debuggingUsingLogOperator() {
        //.checkpoint() operator
        List<String> nameList = Arrays.asList("Rochel", "April", "Hong");
        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .distinct()
                .map(name -> name.substring(0, 3))
                .map(String::toUpperCase)
                .log();

        stringFlux.subscribe(log::info);
    }

    private static void debuggingUsingHook() {
        // do not use in production...
        Hooks.onOperatorDebug();
        List<String> nameList = Arrays.asList("Rochel", "April", "Hong");
        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .distinct()
                .map(name -> name.substring(0, 3))
                .map(String::toUpperCase)
                .map(name -> {
                    if (name.equals("HON")) {
                        throw new RuntimeException("Boom!");
                    } else {
                        return name;
                    }
                });
        stringFlux.subscribe(log::info);
    }

    public Mono<Map> callClient() {
        log.info("calling __debug opertator example");
        debuggingUsingLogOperator();
        debuggingUsingHook();
        return rest.get().uri("/get").retrieve().bodyToMono(Map.class);
    }

}
