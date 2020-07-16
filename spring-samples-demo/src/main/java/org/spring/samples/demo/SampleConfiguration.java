package org.spring.samples.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "sample-demo.custom.enable", matchIfMissing = true)
public class SampleConfiguration {
    // beans definations when this is enabled
    static {
        System.out.println("Initializing the block hound ..........");
        //BlockHound.install();
    }
}
