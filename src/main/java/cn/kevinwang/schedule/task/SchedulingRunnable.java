package cn.kevinwang.schedule.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author wang
 * @create 2024-01-16-21:38
 */
public class SchedulingRunnable implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(SchedulingRunnable.class);
    private Object bean; // 类对象

    private String beanName; // 类名称

    private String methodName; // 方法名称
    @Override
    public void run() {
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            ReflectionUtils.makeAccessible(method);
            method.invoke(bean);
        } catch (Exception e) {
            logger.error("cn kevinwang schedule err!", e);
        }
    }

    public String taskId(){
        return beanName + "_" + methodName;
    }

    public SchedulingRunnable(Object bean, String beanName, String methodName) {
        this.bean = bean;
        this.beanName = beanName;
        this.methodName = methodName;
    }
}
