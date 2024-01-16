package cn.kevinwang.schedule.common;

import cn.kevinwang.schedule.domain.ExecOrder;
import cn.kevinwang.schedule.task.ScheduledTask;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.ApplicationContext;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wang
 * @create 2024-01-16-15:44
 */
public class Constance {

    public static final Map<String, List<ExecOrder>> execOrderMap = new ConcurrentHashMap<>();

    public static final Map<String, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);
    public static class Global{
        public static ApplicationContext applicationContext;

        public static final String LINE = "/";

        public static int schedulePoolSize = 8;     //定时任务执行线程池核心线程数

        public static String ip; // 本机ip

        public static String zkAddress; // zookeeper服务地址；x.x.x.x:2181

        public static String scheduleServerId; // 任务服务ID； 工程名称En

        public static String scheduleServerName; // 任务服务名称； 工程名称Ch

        public static CuratorFramework client ; // zk配置:client

        public static String path_root = "/cn/kevinwang"; // zk配置：根目录

        public static String path_root_exec = path_root + "/exec";

        public static String path_root_server;

        public static String path_root_server_ip;

        public static String path_root_server_ip_clazz;              //[结构标记]类名称
        public static String path_root_server_ip_clazz_method;       //[结构标记]临时节点
        public static String path_root_server_ip_clazz_method_status;//[结构标记]永久节点
        public static String charset = "utf-8";
    }

    public static class InstructStatus{
        public final static int stop = 0;     //停止
        public final static int Start = 1;    //启动
        public final static int Refresh = 2;  //刷新
    }

    public static class BeanName{
        public static final String StarterServerAutoConfigBeanName = "cn-kevinwang-schedule-starterServerAutoConfig";

        public static final String StarterServerPropertiesBeanName = "cn.kevinwang.schedule";

        public static final String SchedulingConfigBeanName = "cn-kevinwang-schedule-schedulingConfig";

        public static final String TaskScheduleBeanName = "cn-kevinwang-schedule-taskSchedule";

        public static final String CronTaskRegisterBeanName = "cn-kevinwang-schedule-cronTaskRegister";
    }
}
