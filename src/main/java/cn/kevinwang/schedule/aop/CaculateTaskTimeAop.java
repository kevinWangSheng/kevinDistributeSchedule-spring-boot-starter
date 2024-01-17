package cn.kevinwang.schedule.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author wang
 * @create 2024-01-17-11:33
 */
@Aspect
@Component
public class CaculateTaskTimeAop {
    private static final Logger logger = LoggerFactory.getLogger(CaculateTaskTimeAop.class);
    @Pointcut("@annotation(cn.kevinwang.schedule.annotation.DcsSchedule)")
    public void pointCut(){}

    // 计算定时任务花费的时间
    @Around("pointCut()")
    public void caculateTaskTime(ProceedingJoinPoint jp) throws NoSuchMethodException {
        long startTime = System.currentTimeMillis();
        Method method = getMethod(jp);
        try {

            jp.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }finally {
            long endTime = System.currentTimeMillis();
            logger.info("cn kevinwang schedule method：{}.{} take time(m)：{}", jp.getTarget().getClass().getSimpleName(), method.getName(), (endTime - startTime));
        }

    }

    private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return getClass(jp).getMethod(methodSignature.getName(),methodSignature.getParameterTypes());
    }

    public Class getClass(ProceedingJoinPoint jp){
        return jp.getTarget().getClass();
    }
}
