package org.example.frequencycontrolstarter;

import org.example.frequencycontrolstarter.FrequencyControlAspect;
import org.example.frequencycontrolstarter.FrequencyControlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(FrequencyControlProperties.class)
public class FrequencyControlAutoConfiguration {

    @Bean
    public FrequencyControlAspect frequencyControlAspect(FrequencyControlProperties properties) {
        return new FrequencyControlAspect(properties);
    }
}
