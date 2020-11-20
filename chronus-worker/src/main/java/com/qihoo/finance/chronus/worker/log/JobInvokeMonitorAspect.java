package com.qihoo.finance.chronus.worker.log;

import com.qihoo.finance.chronus.context.ContextLog4j2Util;
import com.qihoo.finance.chronus.sdk.domain.JobData;
import com.qihoo.finance.chronus.worker.service.JobDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

import static com.qihoo.finance.chronus.common.ChronusConstants.MONITOR_LOGGER_PATTERN;

@Slf4j
@Aspect
public class JobInvokeMonitorAspect {

    @Pointcut("@annotation(com.qihoo.finance.chronus.worker.log.JobInvokeMonitor)")
    private void pointcut() {
    }

    @Around("pointcut() && @annotation(jobInvokeMonitor)")
    public Object advice(ProceedingJoinPoint joinPoint, JobInvokeMonitor jobInvokeMonitor) throws Throwable {
        StopWatch clock = getStopWatchAndStart();
        Object _this = joinPoint.getThis();
        if (!(_this instanceof JobDispatcher)) {
            return joinPoint.proceed();
        }
        JobData jobData = ((JobDispatcher) joinPoint.getThis()).getJobData();
        String methodName = joinPoint.getSignature().getName();
        try {
            log.info("开始{}，jobData:{}  ", methodName, jobData);
            Object result = joinPoint.proceed();
            log.info("结束{}，jobData:{}，result:{}", methodName, jobData, result);
            return result;
        } catch (Throwable e) {
            throw e;
        } finally {
            printExecMonitorLog(jobData, clock, methodName);
        }
    }

    protected StopWatch getStopWatchAndStart() {
        StopWatch clock = new StopWatch();
        clock.start();
        return clock;
    }

    protected void printExecMonitorLog(JobData jobData, StopWatch clock, String methodName) {
        clock.stop();
        log.info(MONITOR_LOGGER_PATTERN,
                jobData.getServiceName(),
                jobData.getTaskName(),
                jobData.getBeanName(),
                ContextLog4j2Util.getRequestNo(),
                methodName,
                clock.getTotalTimeMillis());
    }
}
