package org.spring.samples.demo;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sample-demo.custom")
@Data
public class SampleProperties {
    private boolean enabled = true;
    private String applicationName = "app-name-from-code";
    private List<String> sampleArray = new ArrayList<>();
}
