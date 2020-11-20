package com.qihoo.finance.chronus.core.log.annotation;

import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.log.NodeLogBO;
import com.qihoo.finance.chronus.core.log.NodeLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Collection;

@Slf4j
@Aspect
@Configuration
public class NodeLogAspect {

    @Resource
    private NodeInfo nodeInfo;

    @Resource
    private NodeLogService nodeLogService;

    public NodeLogAspect() {
    }

    @Around("execution(* *(..)) && @annotation(nodeLog)")
    public Object audit(ProceedingJoinPoint pjp, NodeLog nodeLog) throws Throwable {
        NodeLogBO nodeLogBO = NodeLogBO.start(nodeInfo, nodeLog.value());
        boolean sendLogFlag = true;
        try {
            Object result = pjp.proceed();
            if (nodeLog.resultPutContent()) {
                if (result instanceof Collection && CollectionUtils.isEmpty((Collection) result)) {
                    sendLogFlag = false;
                } else {
                    nodeLogBO.stop(nodeLog.message(), JSONObject.toJSONString(result));
                }
            } else {
                nodeLogBO.stop(nodeLog.message());
            }
            return result;
        } catch (Throwable var13) {
            nodeLogBO.stop(var13.getMessage());
            throw var13;
        } finally {
            try {
                if (sendLogFlag) {
                    nodeLogService.submitEvent(nodeLogBO);
                }
            } catch (Exception e) {
                log.error("保存事件日志异常", e);
            }
        }
    }

}
