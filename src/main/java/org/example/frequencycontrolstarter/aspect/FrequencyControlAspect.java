package org.example.frequencycontrolstarter.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.example.frequencycontrolstarter.annotation.FrequencyAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class FrequencyControlAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Around("@annotation(frequencyAnnotation)") // 使用 @Around 可以访问方法参数
    public Object checkFrequency(ProceedingJoinPoint joinPoint, FrequencyAnnotation frequencyAnnotation) throws Throwable {

        String key = frequencyAnnotation.key();
        int maxCount = frequencyAnnotation.maxCount();
        int timeRange = frequencyAnnotation.timeRange();
        String timeUnitStr = frequencyAnnotation.timeUnit();
        TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());

        String cacheKey = "frequency:" + key;

        // 使用 Redis 进行限流
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String count = operations.get(cacheKey);
        if (count != null && Integer.parseInt(count) >= maxCount) {
            log.warn("请求超过限制！key: {}, 当前请求次数: {}, 最大允许次数: {}", key, count, maxCount);
            throw new IllegalStateException("请求次数超限");
        }

        log.info("当前请求合法，key: {}, 当前请求次数: {}, 最大允许次数: {}", key, (count == null ? 0 : Integer.parseInt(count)), maxCount);
        // 在 Redis 中递增计数
        operations.increment(cacheKey, 1);
        redisTemplate.expire(cacheKey, timeRange, timeUnit);
        log.info("限流计数已更新，key: {}, 当前请求次数: {}", key, operations.get(cacheKey));
        return joinPoint.proceed(); // 继续执行目标方法
    }
}
