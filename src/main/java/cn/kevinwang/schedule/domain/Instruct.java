package cn.kevinwang.schedule.domain;

/**执行指令，用于调度任务
 * @author wang
 * @create 2024-01-16-16:34
 */
public class Instruct {
    private String ip;

    private String scheduleServerId;

    private String beanName;

    private String methodName;

    private String corn;

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

    public String getCorn() {
        return corn;
    }

    public void setCorn(String corn) {
        this.corn = corn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
