package cn.kevinwang.schedule.task;

import java.util.concurrent.ScheduledFuture;

/**
 * @author wang
 * @create 2024-01-16-21:52
 */
public class ScheduledTask {
    volatile ScheduledFuture future;

    public void cancel(){
        ScheduledFuture future = this.future;
        if (future == null) return;
        future.cancel(true);
    }

    public boolean isCanceled(){
        ScheduledFuture future = this.future;
        if (future == null) return true;
        return future.isCancelled();
    }
}
