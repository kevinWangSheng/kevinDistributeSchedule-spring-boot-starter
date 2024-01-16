package cn.kevinwang.schedule.task;

import cn.kevinwang.schedule.common.Constance;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

/**
 * @author wang
 * @create 2024-01-16-21:34
 */
@Component(Constance.BeanName.CronTaskRegisterBeanName)
public class CronTaskRegister implements DisposableBean {
    @Resource(name = Constance.BeanName.TaskScheduleBeanName)
    private TaskScheduler taskScheduler;

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public void addTask(SchedulingRunnable task,String cron){
        if(Constance.scheduledTasks.containsKey(task.taskId())){
            removeCronTask(task.taskId());
        }
        CronTask cronTask = new CronTask(task,cron);
        Constance.scheduledTasks.put(task.taskId(),scheduleCronTask(cronTask) );
    }

    public void removeCronTask(String taskId) {
        ScheduledTask scheduledTask = Constance.scheduledTasks.remove(taskId);
        if(scheduledTask == null){
            return;
        }
        scheduledTask.cancel();
    }

    public ScheduledTask scheduleCronTask(CronTask cronTask){
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = taskScheduler.schedule(cronTask.getRunnable(),cronTask.getTrigger());
        return scheduledTask;
    }

    @Override
    public void destroy() throws Exception {
        for(ScheduledTask task : Constance.scheduledTasks.values()){
            task.cancel();
        }
        Constance.scheduledTasks.clear();
    }
}
