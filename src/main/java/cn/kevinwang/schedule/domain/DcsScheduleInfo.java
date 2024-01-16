package cn.kevinwang.schedule.domain;

/**
 * @author wang
 * @create 2024-01-16-16:06
 */
public class DcsScheduleInfo {
    private String ip;

    private String scheduleServerId;

    private String scheduleServerName;

    private String beanName;

    private String methodName;

    private String cron;

    private String desc;

    private int status;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getScheduleServerId() {
        return scheduleServerId;
    }

    public void setScheduleServerId(String scheduleServerId) {
        this.scheduleServerId = scheduleServerId;
    }

    public String getScheduleServerName() {
        return scheduleServerName;
    }

    public void setScheduleServerName(String scheduleServerName) {
        this.scheduleServerName = scheduleServerName;
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

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
