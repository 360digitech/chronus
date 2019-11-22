package com.qihoo.finance.chronus.dispatcher.dubbo.cluster;

import com.alibaba.fastjson.JSON;
import com.qihoo.finance.chronus.common.exception.BusinessException;
import com.qihoo.finance.chronus.common.exception.ChronusErrorCodeEnum;
import com.qihoo.finance.chronus.context.ServiceContext;
import com.qihoo.finance.chronus.dispatcher.dubbo.util.InvocationUtil;
import com.qihoo.finance.chronus.sdk.ChronusSdkProcessor;
import com.qihoo.finance.chronus.sdk.domain.JobConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.apache.dubbo.rpc.Constants.ASYNC_KEY;
import static org.apache.dubbo.rpc.cluster.Constants.*;

/**
 * Created by xiongpu on 2019/9/3.
 */
@Slf4j
public class ChronusCFOBClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public ChronusCFOBClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Result doInvoke(final Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        checkInvokers(invokers, invocation);
        JobConfig jobConfig = InvocationUtil.getJobConfigByArgs(invocation);

        invokers = doSelect(invokers, invocation, jobConfig);
        RpcContext.getContext().setAttachment("requestNo", ServiceContext.getContext().getRequestNo());
        RpcContext.getContext().setInvokers((List) invokers);

        Class<?> returnType;
        try {
            returnType = getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes()).getReturnType();
        } catch (NoSuchMethodException e) {
            returnType = null;
        }

        Map<String, Result> results = new HashMap<>();
        for (final Invoker<T> invoker : invokers) {
            RpcInvocation subInvocation = new RpcInvocation(invocation, invoker);
            subInvocation.setAttachment(ASYNC_KEY, "true");
            results.put(invoker.getUrl().getServiceKey(), invoker.invoke(subInvocation));
        }

        List<Result> resultList = new ArrayList<>(results.size());
        for (Map.Entry<String, Result> entry : results.entrySet()) {
            Result asyncResult = entry.getValue();
            try {
                Result r = asyncResult.get();
                if (r.hasException()) {
                    log.error("Invoke failed: " + r.getException().getMessage(), r.getException());
                } else {
                    resultList.add(r);
                }
            } catch (Exception e) {
                throw new RpcException("Failed to invoke service " + entry.getKey() + ": " + e.getMessage(), e);
            }
        }

        if (returnType == void.class) {
            return AsyncRpcResult.newDefaultAsyncResult(invocation);
        }

        if (resultList.isEmpty()) {
            return AsyncRpcResult.newDefaultAsyncResult(invocation);
        } else {
            return resultList.iterator().next();
        }
    }

    protected List<Invoker<T>> doSelect(List<Invoker<T>> invokers, Invocation invocation, JobConfig jobConfig) {
        if (jobConfig == null || StringUtils.isBlank(jobConfig.getSysCode())) {
            return this.doRandomSelectList(invokers, invocation);
        }

        // or  CommonConstants.REMOTE_APPLICATION_KEY
        List<Invoker<T>> tmpInvokers = invokers.stream().filter(e -> e.getUrl().toFullString().contains("/" + jobConfig.getSysCode() + "/" + ChronusSdkProcessor.class.getName())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(tmpInvokers)) {
            if (jobConfig.isBroadcastInvoker()) {
                return tmpInvokers;
            }
            return this.doRandomSelectList(tmpInvokers, invocation);
        }
        Set<String> urlStrList = invokers.stream().map(e -> e.getUrl().getParameter("application")).collect(Collectors.toSet());
        log.error("获取invoker失败 producerSystemSet:{}   sysCode:{}", JSON.toJSONString(urlStrList), jobConfig.getSysCode());
        throw new BusinessException(ChronusErrorCodeEnum.SYS_CODE_MATCHING_INVOKER_NOT_EXIST.getCode(), ChronusErrorCodeEnum.SYS_CODE_MATCHING_INVOKER_NOT_EXIST.getMsg() + " sysCode:" + jobConfig.getSysCode());
    }


    protected List<Invoker<T>> doRandomSelectList(List<Invoker<T>> invokers, Invocation invocation) {
        List<Invoker<T>> invokerList = new ArrayList<>();
        invokerList.add(doRandomSelect(invokers, invocation));
        return invokerList;
    }

    protected Invoker<T> doRandomSelect(List<Invoker<T>> invokers, Invocation invocation) {
        // Number of invokers
        int length = invokers.size();
        // Every invoker has the same weight?
        boolean sameWeight = true;
        // the weight of every invokers
        int[] weights = new int[length];
        // the first invoker's weight
        int firstWeight = getWeight(invokers.get(0), invocation);
        weights[0] = firstWeight;
        // The sum of weights
        int totalWeight = firstWeight;
        for (int i = 1; i < length; i++) {
            int weight = getWeight(invokers.get(i), invocation);
            // save for later use
            weights[i] = weight;
            // Sum
            totalWeight += weight;
            if (sameWeight && weight != firstWeight) {
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            // Return a invoker based on the random value.
            for (int i = 0; i < length; i++) {
                offset -= weights[i];
                if (offset < 0) {
                    return invokers.get(i);
                }
            }
        }
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return invokers.get(ThreadLocalRandom.current().nextInt(length));
    }


    /**
     * Get the weight of the invoker's invocation which takes warmup time into account
     * if the uptime is within the warmup time, the weight will be reduce proportionally
     *
     * @param invoker    the invoker
     * @param invocation the invocation of this invoker
     * @return weight
     */
    protected int getWeight(Invoker<?> invoker, Invocation invocation) {
        int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), WEIGHT_KEY, DEFAULT_WEIGHT);
        if (weight > 0) {
            long timestamp = invoker.getUrl().getParameter(REMOTE_TIMESTAMP_KEY, 0L);
            if (timestamp > 0L) {
                int uptime = (int) (System.currentTimeMillis() - timestamp);
                int warmup = invoker.getUrl().getParameter(WARMUP_KEY, DEFAULT_WARMUP);
                if (uptime > 0 && uptime < warmup) {
                    weight = calculateWarmupWeight(uptime, warmup, weight);
                }
            }
        }
        return weight >= 0 ? weight : 0;
    }

    /**
     * Calculate the weight according to the uptime proportion of warmup time
     * the new weight will be within 1(inclusive) to weight(inclusive)
     *
     * @param uptime the uptime in milliseconds
     * @param warmup the warmup time in milliseconds
     * @param weight the weight of an invoker
     * @return weight which takes warmup into account
     */
    static int calculateWarmupWeight(int uptime, int warmup, int weight) {
        int ww = (int) ((float) uptime / ((float) warmup / (float) weight));
        return ww < 1 ? 1 : (ww > weight ? weight : ww);
    }
}
