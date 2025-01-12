package org.example.frequencycontrolstarter.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.frequencycontrolstarter.annotation.FrequencyAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class FrequencyControlAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Before("@annotation(frequencyAnnotation)") // 匹配被 @FrequencyAnnotation 注解的方法
    public void checkFrequency(FrequencyAnnotation frequencyAnnotation) {
        String key = getClientIp();
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
    }

    /**
     * 获取客户端的 IP 地址
     */
    private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
