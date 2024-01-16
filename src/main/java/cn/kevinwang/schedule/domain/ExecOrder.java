package cn.kevinwang.schedule.domain;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author wang
 * @create 2024-01-16-16:09
 */
public class ExecOrder {
    @JSONField(serialize = false)
    private Object bean; // 类对象

    private String beanName; // 类名称

    private String methodName; // 方法名称

    private String desc; // 任务描述

    private String corn; // 任务执行表达式

    private boolean autoStatus; // 任务开启状态

    public ExecOrder(Object bean, String beanName, String methodName, String desc, String corn, boolean autoStatus) {
        this.bean = bean;
        this.beanName = beanName;
        this.methodName = methodName;
        this.desc = desc;
        this.corn = corn;
        this.autoStatus = autoStatus;
    }

    public ExecOrder() {
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCorn() {
        return corn;
    }

    public void setCorn(String corn) {
        this.corn = corn;
    }

    public boolean getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(boolean autoStatus) {
        this.autoStatus = autoStatus;
    }
}
