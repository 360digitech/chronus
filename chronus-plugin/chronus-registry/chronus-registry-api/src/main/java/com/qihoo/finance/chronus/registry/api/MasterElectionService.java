package com.qihoo.finance.chronus.registry.api;

/**
 * Master 选举服务
 * Created by xiongpu on 2019/8/30.
 */
public interface MasterElectionService {

    void setMasterGroupByTag(boolean masterGroupByTag);

    /**
     * 节点选举方法
     * 选举规则-> 最先注册的enableMaster=Y & 正常状态的 节点
     * @return 节点地址
     * @throws Exception
     */
    String election(String currentNodeAddress) throws Exception;

    /**
     * 当前的Master是否正常活跃状态
     *
     * @return
     * @throws Exception
     */
    boolean isActiveMaster() throws Exception;

    /**
     * 当前的节点是否为Master
     *
     * @return
     * @throws Exception
     */
    boolean isMaster() throws Exception;

    /**
     * 节点当选为Master
     * 并且更新 currentNode 的version, 触发重新分配
     *
     * @param masterNodeAddress
     * @throws Exception
     */
    void nodeElectedMaster(String masterNodeAddress) throws Exception;

    /**
     * 获取当前Master节点地址
     *
     * @return
     * @throws Exception
     */
    String getCurrentMasterNodeAddress() throws Exception;
}
