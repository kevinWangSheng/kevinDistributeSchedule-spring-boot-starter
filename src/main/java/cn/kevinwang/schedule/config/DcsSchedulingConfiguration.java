package cn.kevinwang.schedule.config;

import cn.kevinwang.schedule.annotation.DcsSchedule;
import cn.kevinwang.schedule.common.Constance;
import cn.kevinwang.schedule.domain.ExecOrder;
import cn.kevinwang.schedule.service.HeartbeatService;
import cn.kevinwang.schedule.service.ZkCuratorService;
import cn.kevinwang.schedule.task.CronTaskRegister;
import cn.kevinwang.schedule.task.SchedulingRunnable;
import cn.kevinwang.schedule.util.StringUtil;
import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cn.kevinwang.schedule.common.Constance.Global.*;

/**
 * @author wang
 * @create 2024-01-16-17:15
 */
@Configuration
public class DcsSchedulingConfiguration implements ApplicationContextAware, BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(DcsSchedulingConfiguration.class);
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Constance.Global.applicationContext = applicationContext;
    }

    // 当bean初始化完成之后，需要将对应的定时任务执行的类装载到内存中，这里用一个map存储，方便后面的调用
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取他对应的最终代理执行类
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if(nonAnnotatedClasses.contains(targetClass)) return bean;
        // 通过反射获取对应的方法
        Method[] declaredMethods = ReflectionUtils.getDeclaredMethods(targetClass);
        if(declaredMethods == null || declaredMethods.length == 0) return bean;
        // 将要执行的任务对象存储到的目标map中
        List<ExecOrder> execOrders = Constance.execOrderMap.computeIfAbsent(beanName, k -> new ArrayList<>());
        // 这个set用来防止重复添加对应的定时任务
        nonAnnotatedClasses.add(targetClass);
        // 抽取出每一个方法，将它转换成为一个执行任务对象
        for(Method method:declaredMethods){
            DcsSchedule dcsSchedule = AnnotationUtils.findAnnotation(method, DcsSchedule.class);
            if(null == dcsSchedule) continue;
            // 将对应的任务进行转化
            ExecOrder execOrder = new ExecOrder();
            execOrder.setBean(bean);
            execOrder.setBeanName(beanName);
            execOrder.setCorn(dcsSchedule.corn());
            execOrder.setDesc(dcsSchedule.desc());
            execOrder.setAutoStatus(dcsSchedule.autoStartup());
            execOrder.setMethodName(method.getName());
            execOrders.add(execOrder);
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            // 1. 初始化配置
            initConfig(applicationContext);
            // 2. 初始化服务
            initServer(applicationContext);
            // 3. 启动任务
            initTask(applicationContext);
            // 4. 挂载节点
            initNode();
            // 5. 心跳监听
            HeartbeatService.getInstance().startFlushScheduleStatus();
            logger.info("cn kevinwang init the schedule config、 server、 task、 node、 heartbeat success!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initConfig(ApplicationContext context){
        try {
            StarterServerProperties properties = context.getBean(Constance.BeanName.StarterServerAutoConfigBeanName, StarterServerAutoConfig.class).getProperties();
            Constance.Global.zkAddress = properties.getZkAddress();
            Constance.Global.scheduleServerId = properties.getScheduleServerId();
            Constance.Global.scheduleServerName = properties.getScheduleServerName();
            InetAddress localHost = InetAddress.getLocalHost();
            Constance.Global.ip = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("cn kevinwang init the schedule config error!", e);
            throw new RuntimeException(e);
        }
    }

    public void initServer(ApplicationContext context){
        try {
            CuratorFramework client = ZkCuratorService.getClient(Constance.Global.zkAddress);
            Constance.Global.path_root_server = StringUtil.join(Constance.Global.path_root, LINE, "server", LINE, Constance.Global.scheduleServerId);
            path_root_server_ip = StringUtil.join(Constance.Global.path_root_server, LINE, "ip", LINE, Constance.Global.ip);
            // 递归删除ip下的旧内容
            ZkCuratorService.deletingChildrenIfNeeded(client, path_root_server_ip);
            // 删除之后重新创建
            ZkCuratorService.createNode(client, path_root_server_ip);
            // 然后在设置对应的数据
            ZkCuratorService.setData(client, Constance.Global.path_root_server, Constance.Global.scheduleServerName);

            // 添加节点的同时进行监听
            ZkCuratorService.createNodeSimple(client,Constance.Global.path_root_exec);
            ZkCuratorService.addTreeCacheListener(context,client,Constance.Global.path_root_exec);
        } catch (Exception e) {
            logger.error("cn kevinwang init the schedule server error!", e);
        }
    }

    // 对所有需要运行的任务进行启动
    public void initTask(ApplicationContext context){
        CronTaskRegister cronTaskRegister = context.getBean(Constance.BeanName.CronTaskRegisterBeanName, CronTaskRegister.class);
        Set<String> beanNames = Constance.execOrderMap.keySet();
        for(String beanName:beanNames){
            List<ExecOrder> execOrders = Constance.execOrderMap.get(beanName);
            for(ExecOrder execOrder:execOrders){
                // 对其判断是否需要启动
                if(!context.containsBean(execOrder.getBeanName())) continue;
                Object bean = context.getBean(execOrder.getBeanName());
                if(execOrder.getAutoStatus()){
                    cronTaskRegister.addTask(new SchedulingRunnable(bean,execOrder.getBeanName(),execOrder.getMethodName()),execOrder.getCorn());
                }
            }
        }
    }

    //4. 挂载节点
    private void initNode() throws Exception {
        Set<String> beanNames = Constance.execOrderMap.keySet();
        for (String beanName : beanNames) {
            List<ExecOrder> execOrderList = Constance.execOrderMap.get(beanName);
            for (ExecOrder execOrder : execOrderList) {
                String path_root_server_ip_clazz = StringUtil.join(path_root_server_ip, LINE, "clazz", LINE, execOrder.getBeanName());
                String path_root_server_ip_clazz_method = StringUtil.join(path_root_server_ip_clazz, LINE, "method", LINE, execOrder.getMethodName());
                String path_root_server_ip_clazz_method_status = StringUtil.join(path_root_server_ip_clazz, LINE, "method", LINE, execOrder.getMethodName(), "/status");
                //添加节点
                ZkCuratorService.createNodeSimple(client, path_root_server_ip_clazz);
                ZkCuratorService.createNodeSimple(client, path_root_server_ip_clazz_method);
                ZkCuratorService.createNodeSimple(client, path_root_server_ip_clazz_method_status);
                //添加节点数据[临时]
                ZkCuratorService.appendPersistentData(client, path_root_server_ip_clazz_method + "/value", JSON.toJSONString(execOrder));
                //添加节点数据[永久]
                ZkCuratorService.setData(client, path_root_server_ip_clazz_method_status, execOrder.getAutoStatus() ? "1" : "0");
            }
        }
    }
}
