package cn.kevinwang.schedule.config;

import cn.kevinwang.schedule.common.Constance;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wang
 * @create 2024-01-16-19:50
 */
@ConfigurationProperties(Constance.BeanName.StarterServerPropertiesBeanName)
public class StarterServerProperties {
    private String zkAddress;

    private String scheduleServerId;

    private String scheduleServerName;

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
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
