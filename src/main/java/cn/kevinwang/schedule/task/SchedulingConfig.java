package cn.kevinwang.schedule.task;

import cn.kevinwang.schedule.common.Constance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @author wang
 * @create 2024-01-16-21:29
 */
@Configuration(Constance.BeanName.SchedulingConfigBeanName)
public class SchedulingConfig {

    @Bean(Constance.BeanName.TaskScheduleBeanName)
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(Constance.Global.schedulePoolSize);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setThreadNamePrefix("CnKevinWangScheduleThreadPool-");
        return taskScheduler;
    }
}
