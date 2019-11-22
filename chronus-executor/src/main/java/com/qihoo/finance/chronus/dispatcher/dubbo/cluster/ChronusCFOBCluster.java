package com.qihoo.finance.chronus.dispatcher.dubbo.cluster;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.Directory;

/**
 * Created by xiongpu on 2019/9/3.
 */
@Slf4j
@SPI(ChronusCFOBCluster.NAME)
public class ChronusCFOBCluster implements Cluster {

    /**
     *
     */
    public final static String NAME = "ChronusCFOB";

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new ChronusCFOBClusterInvoker<T>(directory);
    }

}

