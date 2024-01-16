package cn.kevinwang.schedule.domain;

/**
 * @author wang
 * @create 2024-01-16-16:08
 */
public class DcsServerNode {
    private String scheduleServerId;

    private String scheduleServerName;

    public DcsServerNode(String scheduleServerId, String scheduleServerName) {
        this.scheduleServerId = scheduleServerId;
        this.scheduleServerName = scheduleServerName;
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
}
