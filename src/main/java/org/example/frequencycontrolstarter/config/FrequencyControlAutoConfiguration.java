package org.example.frequencycontrolstarter.config;

import org.example.frequencycontrolstarter.aspect.FrequencyControlAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrequencyControlAutoConfiguration {
    @Bean
    public FrequencyControlAspect frequencyControlAspect() {
        return new FrequencyControlAspect();
    }
}
