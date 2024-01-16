package cn.kevinwang.schedule.config;

import cn.kevinwang.schedule.common.Constance;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wang
 * @create 2024-01-16-19:51
 */
@Configuration(Constance.BeanName.StarterServerAutoConfigBeanName)
@EnableConfigurationProperties(StarterServerProperties.class)
public class StarterServerAutoConfig {

    @Resource
    private StarterServerProperties properties;


    public StarterServerProperties getProperties() {
        return properties;
    }

    public void setProperties(StarterServerProperties properties) {
        this.properties = properties;
    }
}
