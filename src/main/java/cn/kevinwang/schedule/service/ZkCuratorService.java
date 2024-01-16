package cn.kevinwang.schedule.service;

import cn.kevinwang.schedule.common.Constance;
import cn.kevinwang.schedule.domain.Instruct;
import cn.kevinwang.schedule.task.CronTaskRegister;
import cn.kevinwang.schedule.task.SchedulingRunnable;
import cn.kevinwang.schedule.util.StringUtil;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.kevinwang.schedule.common.Constance.Global.LINE;
import static cn.kevinwang.schedule.common.Constance.Global.path_root;

/**
 * @author wang
 * @create 2024-01-16-21:11
 */
public class ZkCuratorService {
    private static final Logger logger = LoggerFactory.getLogger(ZkCuratorService.class);
    private static Object lock = new Object();

    public static CuratorFramework getClient(String connectString){
        if(null != Constance.Global.client){
            return Constance.Global.client;
        }
        // 进行锁
        CuratorFramework client = null;
        synchronized (lock){
            if(null != Constance.Global.client){
                return Constance.Global.client;
            }
            // 创建重试次数
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
            client = CuratorFrameworkFactory.newClient(connectString,retryPolicy);
            // 添加对应的监听器
            client.getConnectionStateListenable().addListener((curatorFramework, connectionState) -> {
                switch (connectionState){
                    case CONNECTED:
                        logger.info("itstack middleware schedule init server connected {}",connectString);
                        break;
                    case RECONNECTED:
                        break;
                    default:
                        break;
                }
            });
            client.start();
        }
        Constance.Global.client = client;
        return client;
    }

    public static void addTreeCacheListener(final ApplicationContext context,final CuratorFramework client,String path) throws Exception {
        TreeCache treeCache = new TreeCache(client,path);
        treeCache.start();
        treeCache.getListenable().addListener((curatorFramework, event) -> {
            if(null == event.getData()) return;
            byte[] data = event.getData().getData();
            if(null == data || data.length == 0) return;
            String json = new String(data,Constance.Global.charset);
            if(json.indexOf('{')!= 0 || json.lastIndexOf('}')!= json.length()+1) return;
            Instruct instruct = JSON.parseObject(json, Instruct.class);
            switch (event.getType()){
                case NODE_ADDED:
                    logger.info("NODE_ADDED : {}",event.getData().getPath());
                    break;
                case NODE_REMOVED:
                    logger.info("NODE_REMOVED : {}",event.getData().getPath());
                    break;
                case NODE_UPDATED:
                    logger.info("NODE_UPDATED : {}",event.getData().getPath());
                    if(Constance.Global.ip.equals(instruct.getIp()) && Constance.Global.scheduleServerId.equals(instruct.getScheduleServerId())){
                        CronTaskRegister cronTaskRegister = context.getBean(Constance.BeanName.CronTaskRegisterBeanName, CronTaskRegister.class);
                        // 判断对应的容器中是否存在对应的要执行任务的bean
                        if (!context.containsBean(instruct.getBeanName())) return;
                        Object scheduleBean = context.getBean(instruct.getBeanName());
                        String path_root_server_ip_clazz_method_status = StringUtil.join(path_root, LINE, "server", LINE, instruct.getScheduleServerId(), LINE, "ip", LINE, instruct.getIp(), LINE, "clazz", LINE, instruct.getBeanName(), LINE, "method", LINE, instruct.getMethodName(), "/status");
                        int status = instruct.getStatus();
                        switch (status){
                            case Constance.InstructStatus.stop:
                                cronTaskRegister.removeCronTask(instruct.getBeanName()+"_"+instruct.getMethodName());
                                setData(client,path_root_server_ip_clazz_method_status,"0");
                                logger.info("cn kevinwang schedule task stop {} {}", instruct.getBeanName(), instruct.getMethodName());
                                break;
                            case Constance.InstructStatus.Start:
                                cronTaskRegister.addTask(new SchedulingRunnable(scheduleBean,instruct.getBeanName(),instruct.getMethodName()),instruct.getCorn());
                                setData(client,path_root_server_ip_clazz_method_status,"1");
                                logger.info("cn kevinwang schedule task start {} {}", instruct.getBeanName(), instruct.getMethodName());
                                break;
                            case Constance.InstructStatus.Refresh:
                                cronTaskRegister.removeCronTask(instruct.getBeanName()+"_"+instruct.getMethodName());
                                cronTaskRegister.addTask(new SchedulingRunnable(scheduleBean,instruct.getBeanName(),instruct.getMethodName()),instruct.getCorn());
                                setData(client,path_root_server_ip_clazz_method_status,"1");
                                logger.info("cn kevinwang schedule task refresh {} {}", instruct.getBeanName(), instruct.getMethodName());
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        });
    }

    public static void setData(CuratorFramework client,String path,String data) throws Exception {
        if(null == client.checkExists().forPath(path)) return;
        client.setData().forPath(path,data.getBytes(Constance.Global.charset));
    }

    public static void createNode(CuratorFramework client,String path) throws Exception {
        List<String> pathChild = new ArrayList<>();
        if(StringUtils.isBlank(path)) return;
        while(path.indexOf(LINE)>0){
            path = path.substring(0,path.lastIndexOf(LINE));
            pathChild.add(path);
        }

        for(int i = pathChild.size()-1;i>=0;i--){
            if(null == client.checkExists().forPath(pathChild.get(i))){
                client.create().creatingParentsIfNeeded().forPath(pathChild.get(i));
            }
        }
    }

    public static void createNodeSimple(CuratorFramework client, String path) throws Exception {
        if (null == client.checkExists().forPath(path)) {
            client.create().creatingParentsIfNeeded().forPath(path);
        }
    }

    public static void deleteNodeSimple(CuratorFramework client, String path) throws Exception {
        if (null != client.checkExists().forPath(path)) {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        }
    }

    public static byte[] getData(CuratorFramework client,String path) throws Exception {
        return client.getData().forPath(path);
    }

    public static void deleteDataRetainNode(CuratorFramework client, String path) throws Exception {
        if (null != client.checkExists().forPath(path)) {
            client.delete().forPath(path);
        }
    }

    public static void appendPersistentData(CuratorFramework client, String path, String data) throws Exception {
        PersistentEphemeralNode node = new PersistentEphemeralNode(client, PersistentEphemeralNode.Mode.EPHEMERAL, path, data.getBytes(Constance.Global.charset));
        node.start();
        node.waitForInitialCreate(3, TimeUnit.SECONDS);
    }

    public static void deletingChildrenIfNeeded(CuratorFramework client, String path) throws Exception {
        if (null == client.checkExists().forPath(path)) return;
        // 递归删除节点
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }
}
