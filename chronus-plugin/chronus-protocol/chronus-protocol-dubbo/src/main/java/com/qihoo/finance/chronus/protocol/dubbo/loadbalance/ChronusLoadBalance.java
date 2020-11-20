package com.qihoo.finance.chronus.protocol.dubbo.loadbalance;

import com.qihoo.finance.chronus.common.exception.ChronusErrorCodeEnum;
import com.qihoo.finance.chronus.context.ServiceContext;
import com.qihoo.finance.chronus.protocol.exception.NoProviderException;
import com.qihoo.finance.chronus.sdk.ChronusSdkFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.cluster.loadbalance.RandomLoadBalance;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/9/3.
 */
@Slf4j
@SPI(ChronusLoadBalance.NAME)
public class ChronusLoadBalance extends RandomLoadBalance {
    public static final String NAME = "ChronusLoadBalance";

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        return !CollectionUtils.isEmpty(invokers) ? this.doSelect(invokers, url, invocation) : null;
    }

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        String serviceName = ServiceContext.getContext().getServiceName();
        Assert.notNull(serviceName, "ServiceContext ServiceName Cannot be empty");
        RpcContext.getContext().setAttachment("requestNo", ServiceContext.getContext().getRequestNo());

        // or  CommonConstants.REMOTE_APPLICATION_KEY
        List<Invoker<T>> tmpInvokers = invokers.stream().filter(e -> e.getUrl().toFullString().contains("/" + serviceName + "/" + ChronusSdkFacade.class.getName())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tmpInvokers)) {
            log.warn("doSelect invokers url fullPath not exist '{}'", serviceName);
            tmpInvokers = invokers.stream().filter(e -> serviceName.equalsIgnoreCase(e.getUrl().getParameter("ServerApplicationName"))).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(tmpInvokers)) {
            log.warn("doSelect invokers url parameter['ServerApplicationName'] not exist '{}'", serviceName);
            tmpInvokers = invokers.stream().filter(e -> serviceName.equalsIgnoreCase(e.getUrl().getParameter("application"))).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(tmpInvokers)) {
            return super.doSelect(tmpInvokers, url, invocation);
        }
        throw new NoProviderException(ChronusErrorCodeEnum.CALL_BIZ_NO_PROVIDER, "serviceName:" + serviceName);
    }
}
