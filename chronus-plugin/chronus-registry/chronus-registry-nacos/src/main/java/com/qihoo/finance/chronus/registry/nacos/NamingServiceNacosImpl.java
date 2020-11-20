package com.qihoo.finance.chronus.registry.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import com.qihoo.finance.chronus.registry.util.InstanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Slf4j
public class NamingServiceNacosImpl implements NamingService {

    @NacosInjected
    private com.alibaba.nacos.api.naming.NamingService nacosNamingService;

    /* */
    //@NacosInjected
    //https://github.com/nacos-group/nacos-spring-project/issues/144
    @Resource
    private NamingMaintainService namingMaintainService;
    /* */

    @Resource
    private NodeInfo currentNode;

    @Override
    public void registerNode() throws Exception {
        Instance instance = new Instance();
        instance.setInstanceId(currentNode.getAddress());
        instance.setIp(currentNode.getIp());
        instance.setClusterName(currentNode.getCluster());
        instance.setPort(currentNode.getPort());
        Map<String, String> metadata = new HashMap<>();
        metadata.put(ChronusConstants.HOST_NAME, currentNode.getHostName());
        metadata.put(ChronusConstants.REGISTER_TIME, currentNode.getVersion());
        metadata.put(ChronusConstants.CLUSTER, currentNode.getCluster());
        if (Objects.equals(ChronusConstants.Y, currentNode.getEnableMaster())) {
            metadata.put(ChronusConstants.IS_MASTER, ChronusConstants.N);
        }
        metadata.put(ChronusConstants.ENABLE_MASTER, currentNode.getEnableMaster());
        metadata.put(ChronusConstants.ENABLE_WORKER, currentNode.getEnableWorker());
        metadata.put(ChronusConstants.TAG, currentNode.getTag());
        metadata.put(ChronusConstants.DATA_VERSION, currentNode.getVersion());
        instance.setMetadata(metadata);
        nacosNamingService.registerInstance(ChronusConstants.NODE_NAME_CHRONUS, instance);
        //https://github.com/nacos-group/nacos-spring-project/issues/144
        //namingMaintainService = NamingMaintainFactory.createMaintainService(environment.getProperty("nacos.discovery.server-addr"));
    }

    @Override
    public boolean currentNodeIsActive() throws Exception {
        return "UP".equals(nacosNamingService.getServerStatus());
    }

    @Override
    public Node getCurrentNode() throws Exception {
        List<String> clusters = new ArrayList<>(1);
        clusters.add(currentNode.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);

        Instance instance = instanceList.stream().filter(e -> e.getInstanceId().equals(currentNode.getAddress())).findFirst().orElse(null);
        return instance != null ? convertNode(instance) : null;
    }

    /**
     * 获取当前集群的所有Worker节点
     * @return
     * @throws Exception
     */
    @Override
    public List<Node> getAllWorkerNode() throws Exception {
        List<String> clusters = new ArrayList<>(1);
        clusters.add(currentNode.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);
        if (CollectionUtils.isEmpty(instanceList)) {
            return new ArrayList<>();
        }

        instanceList = instanceList.stream().filter(e -> e.isHealthy() && ChronusConstants.Y.equals(e.getMetadata().get(ChronusConstants.ENABLE_WORKER))).collect(Collectors.toList());
        List<Node> result = new ArrayList<>();
        for (Instance instance : instanceList) {
            result.add(convertNode(instance));
        }
        return result;
    }

    /**
     * 获取指定集群的所有节点
     * @param cluster
     * @return
     * @throws Exception
     */
    @Override
    public List<Node> getAllNode(String cluster) throws Exception {
        List<String> clusters = new ArrayList<>(1);
        clusters.add(currentNode.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);
        if (CollectionUtils.isEmpty(instanceList)) {
            return new ArrayList<>();
        }

        List<Node> result = new ArrayList<>();
        for (Instance instance : instanceList) {
            result.add(convertNode(instance));
        }
        return result;
    }


    /**
     * 获取所有集群的所有节点
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<Node> getAllNode() throws Exception {
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS);
        if (CollectionUtils.isEmpty(instanceList)) {
            return new ArrayList<>();
        }
        List<Node> result = new ArrayList<>();
        for (Instance instance : instanceList) {
            Node node = convertNode(instance);
            result.add(node);
        }
        return result;
    }


    private Node convertNode(Instance instance) {
        Node node = new Node();
        node.setHostName(instance.getMetadata().get(ChronusConstants.HOST_NAME));
        node.setCluster(instance.getMetadata().get(ChronusConstants.CLUSTER));
        node.setEnableWorker(instance.getMetadata().get(ChronusConstants.ENABLE_WORKER));
        node.setEnableMaster(instance.getMetadata().get(ChronusConstants.ENABLE_MASTER));
        node.setIsMaster(instance.getMetadata().get(ChronusConstants.IS_MASTER));
        node.setVersion(instance.getMetadata().get(ChronusConstants.REGISTER_TIME));
        node.setDataVersion(instance.getMetadata().get(ChronusConstants.DATA_VERSION));
        node.setTag(instance.getMetadata().get(ChronusConstants.TAG));
        node.setAddress(InstanceUtils.getAddressByInstance(instance));
        if (!instance.isEnabled()) {
            node.setState(NodeStateEnum.OFFLINE.getState());
        } else {
            node.setState(instance.isHealthy() ? NodeStateEnum.NORMAL.getState() : NodeStateEnum.DEAD.getState());
        }
        return node;
    }

    @Override
    public void offlineNode(Node node) throws Exception {
        List<String> clusters = new ArrayList<>(1);
        clusters.add(node.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);
        if (CollectionUtils.isEmpty(instanceList)) {
            return;
        }
        Instance instance = instanceList.stream().filter(e -> InstanceUtils.getAddressByInstance(e).equals(node.getAddress())).findFirst().orElse(null);
        if (instance != null) {
            instance.setEnabled(false);
            if (Objects.equals(ChronusConstants.Y, instance.getMetadata().get(ChronusConstants.ENABLE_MASTER))) {
                instance.getMetadata().put(ChronusConstants.IS_MASTER, ChronusConstants.N);
            }
            namingMaintainService.updateInstance(ChronusConstants.NODE_NAME_CHRONUS, instance);
        }
    }

    @Override
    public void onlineNode(Node node) throws Exception {
        List<String> clusters = new ArrayList<>(1);
        clusters.add(node.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);
        if (CollectionUtils.isEmpty(instanceList)) {
            return;
        }
        Instance instance = instanceList.stream().filter(e -> InstanceUtils.getAddressByInstance(e).equals(node.getAddress())).findFirst().orElse(null);
        if (instance != null) {
            instance.setEnabled(true);
            instance.getMetadata().put(ChronusConstants.REGISTER_TIME, String.valueOf(System.currentTimeMillis()));
            instance.getMetadata().put(ChronusConstants.DATA_VERSION, String.valueOf(System.currentTimeMillis()));
            namingMaintainService.updateInstance(ChronusConstants.NODE_NAME_CHRONUS, instance);
        }
    }
}
