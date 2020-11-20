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
     * 获取所有的执行机节点
     * 包括下线的节点
     *
     * @return
     * @throws Exception
     */
    List<Node> getAllWorkerNode() throws Exception;

    /**
     * 获取指定集群的所有节点
     *
     * @param cluster
     * @return
     * @throws Exception
     */
    List<Node> getAllNode(String cluster) throws Exception;

    /**
     * 获取所有集群的所有节点
     * @return
     * @throws Exception
     */
    List<Node> getAllNode() throws Exception;

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
