package com.qihoo.finance.chronus.registry.api;

import java.util.List;

/**
 * Created by xiongpu on 2019/9/21.
 */
public interface NamingService {

    /**
     * 注册一个节点
     *
     * @throws Exception
     */
    void registerNode() throws Exception;

    /**
     * 节点是否正常活跃状态
     *
     * @return
     * @throws Exception
     */
    boolean currentNodeIsActive() throws Exception;

    Node getCurrentNode() throws Exception;

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

    /**
     * 获取所有的执行机节点
     * 包括下线的节点
     *
     * @return
     * @throws Exception
     */
    List<Node> getAllExecutorNode() throws Exception;

    /**
     * 获取所有的节点
     *
     * @param cluster
     * @return
     * @throws Exception
     */
    List<Node> getAllNode(String cluster) throws Exception;

    /**
     * 设置节点的tag标签
     *
     * @param node
     * @throws Exception
     */
    void setTag(Node node) throws Exception;

    /**
     * 下线节点
     *
     * @param node
     * @throws Exception
     */
    void offlineNode(Node node) throws Exception;

    /**
     * 上线节点
     *
     * @param node
     * @throws Exception
     */
    void onlineNode(Node node) throws Exception;
}
