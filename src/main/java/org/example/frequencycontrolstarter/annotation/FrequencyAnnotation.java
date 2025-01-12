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

    String key() default "";
    int maxCount() default 10;
    int timeRange() default 60;
    String timeUnit() default "SECONDS";
}