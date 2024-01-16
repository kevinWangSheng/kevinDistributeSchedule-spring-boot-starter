package cn.kevinwang.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wang
 * @create 2024-01-16-15:41
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DcsSchedule {
    String desc() default "缺省"; // 定时任务描述

    String corn() default "";  // 定时任务表达式

    boolean autoStartup() default true; // 是否启动定时任务
}
