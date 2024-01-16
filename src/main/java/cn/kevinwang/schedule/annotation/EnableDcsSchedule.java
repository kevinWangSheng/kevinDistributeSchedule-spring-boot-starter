package cn.kevinwang.schedule.annotation;

import cn.kevinwang.schedule.config.DcsSchedulingConfiguration;
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
@ImportAutoConfiguration
@ComponentScan("cn.kevinwang.*")
public @interface EnableDcsSchedule {
}
