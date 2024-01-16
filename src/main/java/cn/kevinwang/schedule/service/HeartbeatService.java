package cn.kevinwang.schedule.service;

import cn.kevinwang.schedule.common.Constance;
import cn.kevinwang.schedule.domain.ExecOrder;
import cn.kevinwang.schedule.task.ScheduledTask;
import cn.kevinwang.schedule.util.StringUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.kevinwang.schedule.common.Constance.Global.*;

/**
 * @author wang
 * @create 2024-01-17-1:02
 */
public class HeartbeatService {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    private ScheduledExecutorService ses;

    private static class SingletonHolder{
        private static final HeartbeatService INSTANCE = new HeartbeatService();
    }

    public static HeartbeatService getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void startFlushScheduleStatus() {
        ses = Executors.newScheduledThreadPool(1);
        //300秒后，每60秒心跳一次
        ses.scheduleAtFixedRate(() -> {
            try {
                logger.info("itstack middleware schedule heart beat On-Site Inspection task");
                Map<String, ScheduledTask> scheduledTasks = Constance.scheduledTasks;
                Map<String, List<ExecOrder>> execOrderMap = Constance.execOrderMap;
                Set<String> beanNameSet = execOrderMap.keySet();
                for (String beanName : beanNameSet) {
                    List<ExecOrder> execOrderList = execOrderMap.get(beanName);
                    for (ExecOrder execOrder : execOrderList) {
                        String taskId = execOrder.getBeanName() + "_" + execOrder.getMethodName();
                        ScheduledTask scheduledTask = scheduledTasks.get(taskId);
                        if (null == scheduledTask) continue;
                        boolean cancelled = scheduledTask.isCanceled();
                        // 路径拼装
                        String path_root_server_ip_clazz = StringUtil.join(path_root_server_ip, LINE, "clazz", LINE, execOrder.getBeanName());
                        String path_root_server_ip_clazz_method = StringUtil.join(path_root_server_ip_clazz, LINE, "method", LINE, execOrder.getMethodName(), LINE, "value");
                        // 获取现有值
                        ExecOrder oldExecOrder;
                        byte[] bytes = client.getData().forPath(path_root_server_ip_clazz_method);
                        if (null != bytes) {
                            String oldJson = new String(bytes, charset);
                            oldExecOrder = JSON.parseObject(oldJson, ExecOrder.class);
                        } else {
                            oldExecOrder = new ExecOrder();
                            oldExecOrder.setBeanName(execOrder.getBeanName());
                            oldExecOrder.setMethodName(execOrder.getMethodName());
                            oldExecOrder.setDesc(execOrder.getDesc());
                            oldExecOrder.setCorn(execOrder.getCorn());
                            oldExecOrder.setAutoStatus(execOrder.getAutoStatus());
                        }
                        oldExecOrder.setAutoStatus(!cancelled);
                        //临时节点[数据]
                        if (null == Constance.Global.client.checkExists().forPath(path_root_server_ip_clazz_method))
                            continue;
                        String newJson = JSON.toJSONString(oldExecOrder);
                        Constance.Global.client.setData().forPath(path_root_server_ip_clazz_method, newJson.getBytes(charset));
                        //永久节点[数据]
                        String path_root_ip_server_clazz_method_status = StringUtil.join(path_root_server_ip_clazz, LINE, "method", LINE, execOrder.getMethodName(), "/status");
                        if (null == Constance.Global.client.checkExists().forPath(path_root_ip_server_clazz_method_status))
                            continue;
                        Constance.Global.client.setData().forPath(path_root_ip_server_clazz_method_status, (execOrder.getAutoStatus() ? "1" : "0").getBytes(charset));
                    }
                }
            } catch (Exception ignore) {
            }

        }, 300, 60, TimeUnit.SECONDS);
    }
}
