package com.qihoo.finance.chronus.registry.api;

/**
 * Master 选举服务
 * Created by xiongpu on 2019/8/30.
 */
public interface MasterElectionService {

    /**
     * 节点选举方法
     * 选举规则-> 最先注册的enableMaster=Y & 正常状态的 节点
     * @return 节点地址
     * @throws Exception
     */
    String election() throws Exception;
}
