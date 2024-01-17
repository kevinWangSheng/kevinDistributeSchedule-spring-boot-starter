package cn.kevinwang.schedule.annotation;

import cn.kevinwang.schedule.aop.CaculateTaskTimeAop;
import cn.kevinwang.schedule.config.DcsSchedulingConfiguration;
import cn.kevinwang.schedule.task.CronTaskRegister;
import cn.kevinwang.schedule.task.ScheduledTask;
import cn.kevinwang.schedule.task.SchedulingConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wang
 * @create 2024-01-16-15:39
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({DcsSchedulingConfiguration.class})
@ImportAutoConfiguration({SchedulingConfig.class, CaculateTaskTimeAop.class, CronTaskRegister.class})
@ComponentScan("cn.kevinwang.schedule.*")
public @interface EnableDcsSchedule {
}
