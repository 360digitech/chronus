package com.qihoo.finance.chronus.registry.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import com.qihoo.finance.chronus.registry.util.ZookeeperManager;
import com.qihoo.finance.chronus.registry.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Slf4j
public class NamingServiceZookeeperImpl implements NamingService {

    @Resource
    protected ZookeeperManager zkManager;

    @Resource
    private NodeInfo currentNode;

    private String nodePath;
    private String rootPath;

    @Override
    public void registerNode() throws Exception {
        zkManager.initial();
        rootPath = getRootPath();
        nodePath = ZookeeperUtil.getFullNodePath(rootPath, currentNode.getAddress(), currentNode.getVersion());
        // /chronus/node/cluster/10.143.226.121:7010$402A53C9E43945A9BF65A5BF0BEE5BCD
        if (zkManager.checkExists(nodePath)) {
            zkManager.deleteTree(nodePath);
        }
        nodePath = zkManager.createPath(nodePath, CreateMode.EPHEMERAL);

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
        zkManager.setData(nodePath, JSONObject.toJSONString(node).getBytes());
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
        if (this.nodePath == null || !zkManager.checkExists(nodePath)) {
            return null;
        }
        byte[] data = zkManager.getData(nodePath);
        if (data != null) {
            return convertNode(data, nodePath);
        }
        return null;
    }

    @Override
    public List<Node> getAllWorkerNode() throws Exception {
        String tmpRootPath = getRootPath();
        List<Node> result = getAllNodeByRootPath(tmpRootPath);
        return result.stream().filter(node -> ChronusConstants.Y.equals(node.getEnableWorker())).collect(Collectors.toList());
    }

    @Override
    public List<Node> getAllNode(String cluster) throws Exception {
        String tmpRootPath = ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), cluster);
        List<Node> result = getAllNodeByRootPath(tmpRootPath);
        return result;
    }

    @Override
    public List<Node> getAllNode() throws Exception {
        List<Node> result = new ArrayList<>();
        List<String> clusterPaths = zkManager.getChildren(zkManager.getRootPath());
        for (String cluster : clusterPaths) {
            String tmpRootPath = ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), cluster);
            List<Node> clusterNode = getAllNodeByRootPath(tmpRootPath);
            result.addAll(clusterNode);
        }
        return result;
    }

    private List<Node> getAllNodeByRootPath(String rootPath) throws Exception {
        List<Node> result = new ArrayList<>();
        List<String> nodePaths = zkManager.getChildren(rootPath);
        for (String tpath : nodePaths) {
            byte[] data = zkManager.getData(String.join("/", rootPath, tpath));
            if (data != null) {
                Node node = convertNode(data, tpath);
                result.add(node);
            }
        }
        return result;
    }

    private Node convertNode(byte[] data, String nodePath) {
        Node node = JSONObject.parseObject(new String(data)).toJavaObject(Node.class);
        node.setVersion(nodePath.split("\\$")[1]);
        return node;
    }

    @Override
    public void offlineNode(Node node) throws Exception {
        String tmpRootPath = getRootPath();
        String tmpNodePath = ZookeeperUtil.getFullNodePath(tmpRootPath, node.getAddress(), node.getVersion());

        if (zkManager.checkExists(tmpNodePath)) {
            byte[] data = zkManager.getData(tmpNodePath);
            if (data != null) {
                Node zkNode = convertNode(data, tmpNodePath);
                zkNode.setState(NodeStateEnum.OFFLINE.getState());
                if (Objects.equals(ChronusConstants.Y, zkNode.getEnableMaster())) {
                    zkNode.setIsMaster(ChronusConstants.N);
                }
                zkManager.setData(tmpNodePath, JSONObject.toJSONString(zkNode).getBytes());
            }
        }
    }

    @Override
    public void onlineNode(Node node) throws Exception {
        String tmpRootPath = getRootPath();
        String tmpNodePath = ZookeeperUtil.getFullNodePath(tmpRootPath, node.getAddress(), node.getVersion());
        if (zkManager.checkExists(tmpNodePath)) {
            byte[] data = zkManager.getData(tmpNodePath);
            if (data != null) {
                Node zkNode = convertNode(data, tmpNodePath);
                zkNode.setState(NodeStateEnum.NORMAL.getState());
                zkNode.setRegisterTime(String.valueOf(System.currentTimeMillis()));
                zkNode.setDataVersion(zkNode.getRegisterTime());
                zkManager.setData(tmpNodePath, JSONObject.toJSONString(zkNode).getBytes());
            }
        }
    }

    private String getRootPath() {
        return ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), currentNode.getCluster());
    }

}
