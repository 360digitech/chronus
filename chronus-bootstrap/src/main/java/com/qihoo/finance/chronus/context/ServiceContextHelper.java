package com.qihoo.finance.chronus.context;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.util.NetUtils;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;

import java.util.concurrent.atomic.AtomicLong;

public class ServiceContextHelper {

    public static void initContext() {
        ServiceContext serviceContext = ServiceContext.getContext();
        serviceContext.setRequestNo(ServiceContext.genUniqueId());
        ContextLog4j2Util.addContext2ThreadContext();
    }


    /**
     * 根据任务运行信息初始化当前上下文
     * @param taskRuntime
     */
    public static void initContext(TaskRuntimeEntity taskRuntime) {
        ServiceContext ctx = ServiceContext.getContext();
        ctx.clearContextVar();
        ctx.setRequestNo(ServiceContext.genUniqueId());
        ctx.setLocalIp(NetUtils.getLocalIP());
        //ctx.setSysCode(taskRuntime.getServiceName());
        ctx.setBeanName(taskRuntime.getTaskItemId());
        ContextLog4j2Util.addContext2ThreadContext(ctx);
    }


    /**
     * 按任务信息 初始化上下文
     * @param currentNode
     * @param taskEntity
     * @param requestNo
     * @param totalCount
     */
    public static void initContext(NodeInfo currentNode, TaskEntity taskEntity, String requestNo, AtomicLong totalCount) {
        ServiceContext ctx = ServiceContext.getContext();
        ctx.clearContextVar();
        ctx.setRequestNo(String.format(requestNo + "#%s", totalCount != null ? totalCount.intValue() : 0));
        ctx.setLocalIp(currentNode.getAddress());
        ctx.setSysCode(taskEntity.getDealSysCode());
        ctx.setBeanName(taskEntity.getDealBizBeanName());
        ContextLog4j2Util.addContext2ThreadContext(ctx);
    }

}
