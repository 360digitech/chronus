package com.qihoo.finance.chronus.registry.local;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import com.qihoo.finance.chronus.registry.util.LocalRegistryContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Slf4j
public class NamingServiceLocalImpl implements NamingService {

    @Resource
    private NodeInfo currentNode;

    @Override
    public void registerNode() throws Exception {
        Node node = new Node();
        node.setRegisterTime(String.valueOf(System.currentTimeMillis()));
        if (Objects.equals(ChronusConstants.Y, currentNode.getEnableMaster())) {
            node.setIsMaster(ChronusConstants.N);
        }
        node.setEnableMaster(currentNode.getEnableMaster());
        node.setEnableWorker(currentNode.getEnableWorker());
        node.setState(NodeStateEnum.NORMAL.getState());
        node.setAddress(currentNode.getAddress());
        node.setHostName(currentNode.getHostName());
        node.setTag(currentNode.getTag());
        node.setCluster(currentNode.getCluster());
        node.setDataVersion(currentNode.getVersion());
        LocalRegistryContext.setNode(node);
    }

    @Override
    public boolean currentNodeIsActive() throws Exception {
        Node node = getCurrentNode();
        if (node == null) {
            return false;
        }
        return NodeStateEnum.isNormal(node.getState());
    }

    @Override
    public Node getCurrentNode() throws Exception {
        return LocalRegistryContext.getNode();
    }

    @Override
    public List<Node> getAllWorkerNode() throws Exception {
        List<Node> result = new ArrayList<>();
        result.add(LocalRegistryContext.getNode());
        return result.stream().filter(node -> ChronusConstants.Y.equals(node.getEnableWorker())).collect(Collectors.toList());
    }

    @Override
    public List<Node> getAllNode(String cluster) throws Exception {
        List<Node> result = new ArrayList<>();
        result.add(LocalRegistryContext.getNode());
        result.stream().filter(e -> Objects.equals(cluster, e.getCluster())).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<Node> getAllNode() throws Exception {
        List<Node> result = new ArrayList<>();
        result.add(LocalRegistryContext.getNode());
        return result;
    }


    @Override
    public void offlineNode(Node node) throws Exception {
        LocalRegistryContext.getNode().setState(NodeStateEnum.OFFLINE.getState());
        if (Objects.equals(ChronusConstants.Y, LocalRegistryContext.getNode().getEnableMaster())) {
            LocalRegistryContext.getNode().setIsMaster(ChronusConstants.N);
        }
    }

    @Override
    public void onlineNode(Node node) throws Exception {
        if (LocalRegistryContext.getNode() != null) {
            LocalRegistryContext.getNode().setState(NodeStateEnum.NORMAL.getState());
            String registerTime = String.valueOf(System.currentTimeMillis());
            LocalRegistryContext.getNode().setRegisterTime(registerTime);
            LocalRegistryContext.getNode().setDataVersion(registerTime);
        }
    }
}
