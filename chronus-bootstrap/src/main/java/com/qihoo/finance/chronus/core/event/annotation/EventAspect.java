package com.qihoo.finance.chronus.core.event.annotation;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.event.bo.EventBO;
import com.qihoo.finance.chronus.core.event.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Slf4j
@Aspect
@Configuration
public class EventAspect {

    @Resource
    private NodeInfo nodeInfo;

    @Resource
    private EventService eventService;

    public EventAspect() {
    }

    @Around("execution(* *(..)) && @annotation(event)")
    public Object audit(ProceedingJoinPoint pjp, Event event) throws Throwable {
        EventBO eventBO = EventBO.start(nodeInfo, event.value());
        try {
            Object result = pjp.proceed();
            if (event.resultPutContent()) {
                eventBO.stop(event.message(), result.toString());
            } else {
                eventBO.stop(event.message());
            }
            return result;
        } catch (Throwable var13) {
            eventBO.stop(var13.getMessage());
            throw var13;
        } finally {
            try {
                eventService.submitEvent(eventBO);
            } catch (Exception e) {
                log.error("保存事件日志异常", e);
            }
        }
    }

}
