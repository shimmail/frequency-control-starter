package org.example.frequencycontrolstarter.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)  // 该注解只能应用于方法
@Retention(RetentionPolicy.RUNTIME)  // 在运行时可以通过反射获取
public @interface FrequencyAnnotation {
    int maxCount() default 10; // 单位频控时间范围内最大访问次数
    int timeRange() default 5; // 频控时间范围
    String timeUnit() default "SECONDS"; // 频控时间单位
}