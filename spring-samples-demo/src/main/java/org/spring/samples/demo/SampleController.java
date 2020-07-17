package org.spring.samples.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;

@RestController
@Log4j2
public class SampleController {

    @Autowired
    CustomHttpClient customHttpClient;

    @GetMapping("/ping")
    public Map ping() {
        log.info("Inside the ping method");
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "ok");
        return resp;
    }


    @GetMapping("/blocking/{id}")
    public Mono<ServerResponse> block(@PathVariable String id) {
        log.info("Inside the block method" + id);
        if (!validate(id)) {
            return ServerResponse.badRequest().build();
        }
        return ServerResponse.noContent().build();
    }

    private boolean validate(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }


    @GetMapping("/mono")
    public Mono<Map> mono() {
        log.info("Inside the mono method...");
        return Mono.just(ping());
    }

    @GetMapping("/flux")
    public Flux<Map> flux() {
        log.info("Inside the flux method");
        return Flux.just(ping());
    }

    @GetMapping("/client")
    public Mono<Map> client() {
        log.info("calling client ...");
        return customHttpClient.callClient();
    }

}
