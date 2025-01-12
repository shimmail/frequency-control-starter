package org.example.frequencycontrolstarter.aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.frequencycontrolstarter.annotation.FrequencyAnnotation;
import org.example.frequencycontrolstarter.util.FrequencyControlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class FrequencyControlAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 正确的切点定义和通知方法
    @Before("@annotation(frequencyAnnotation)") // 匹配被 @FrequencyAnnotation 注解的方法
    public void checkFrequency(FrequencyAnnotation frequencyAnnotation) {
        String key = frequencyAnnotation.key();
        int maxCount = frequencyAnnotation.maxCount();
        int timeRange = frequencyAnnotation.timeRange();
        String timeUnit = frequencyAnnotation.timeUnit();
        // 你的频控逻辑
        String cacheKey = "frequency:" + key;

        // 使用 Redis 进行限流的简单示例
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String count = operations.get(cacheKey);
        if (count != null && Integer.parseInt(count) >= maxCount) {
            throw new IllegalStateException("请求次数超限");
        }

        // 在 Redis 中递增计数
        operations.increment(cacheKey, 1);
        redisTemplate.expire(cacheKey, timeRange, TimeUnit.SECONDS);
    }
}
