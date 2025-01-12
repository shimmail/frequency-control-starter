package org.example.frequencycontrolstarter.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FrequencyControlUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 判断请求是否超过限流限制
     */
    public boolean isRequestExceeded(String redisKey, int timeRange, int maxAccessCount, TimeUnit timeUnit) {
        String value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            // 如果没有该 key，说明是第一次请求，设置过期时间并初始化请求次数
            redisTemplate.opsForValue().set(redisKey, "1", timeRange, timeUnit);
            return false;
        }

        // 获取当前的请求次数
        int currentCount = Integer.parseInt(value);
        if (currentCount >= maxAccessCount) {
            // 超过次数限制
            return true;
        } else {
            // 未超过次数，增加计数
            redisTemplate.opsForValue().increment(redisKey);
            return false;
        }
    }
}
