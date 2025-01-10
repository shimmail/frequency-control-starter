package org.example.frequencycontrolstarter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frequency-control")
public class FrequencyControlProperties {

    private String keyPrefix = "frequency:";
    private int timeRange = 60;  // 默认一分钟
    private String timeUnit = "SECONDS";  // 默认单位是秒
    private int maxAccessCount = 10;  // 默认最大访问次数

}

