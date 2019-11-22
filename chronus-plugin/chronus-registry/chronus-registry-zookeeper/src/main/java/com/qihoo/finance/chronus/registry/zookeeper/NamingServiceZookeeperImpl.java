package com.qihoo.finance.chronus.registry.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.event.annotation.Event;
import com.qihoo.finance.chronus.core.event.enums.EventEnum;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import com.qihoo.finance.chronus.registry.util.ZookeeperManager;
import com.qihoo.finance.chronus.registry.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;

import javax.annotation.Resource;
import java.util.*;

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
        rootPath = ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), currentNode.getCluster());
        nodePath = ZookeeperUtil.getFullNodePath(rootPath, currentNode.getAddress(), currentNode.getVersion());
        // /chronus/node/cluster/10.143.226.121:7010$402A53C9E43945A9BF65A5BF0BEE5BCD
        if (zkManager.checkExists(nodePath)) {
            zkManager.deleteTree(nodePath);
        }
        nodePath = zkManager.createPath(nodePath, CreateMode.EPHEMERAL);

        Map<String, String> metadata = new HashMap<>();
        metadata.put(ChronusConstants.REGISTER_TIME, String.valueOf(System.currentTimeMillis()));
        if (Objects.equals(ChronusConstants.Y, currentNode.getEnableMaster())) {
            metadata.put(ChronusConstants.IS_MASTER, ChronusConstants.N);
        }
        metadata.put(ChronusConstants.ENABLE_MASTER, currentNode.getEnableMaster());
        metadata.put(ChronusConstants.ENABLE_EXECUTOR, currentNode.getEnableExecutor());
        metadata.put(ChronusConstants.STATE, NodeStateEnum.NORMAL.getState());
        metadata.put(ChronusConstants.ADDRESS, currentNode.getAddress());
        metadata.put(ChronusConstants.HOST_NAME, currentNode.getHostName());
        metadata.put(ChronusConstants.TAG, ChronusConstants.DEF_TAG);
        metadata.put(ChronusConstants.DATA_VERSION, currentNode.getVersion());
        zkManager.setData(nodePath, JSONObject.toJSONString(metadata).getBytes());
    }


    @Override
    public boolean currentNodeIsActive() throws Exception {
        if (zkManager.checkExists(nodePath)) {
            byte[] data = zkManager.getData(nodePath);
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                return dataObj.containsKey(ChronusConstants.STATE) && NodeStateEnum.isNormal(dataObj.getString(ChronusConstants.STATE));
            }
        }
        return false;
    }

    @Override
    public Node getCurrentNode() throws Exception {
        if (!zkManager.checkExists(nodePath)) {
            return null;
        }
        byte[] data = zkManager.getData(nodePath);
        if (data != null) {
            JSONObject dataObj = JSONObject.parseObject(new String(data));
            Node node = new Node();
            node.setAddress(dataObj.getString(ChronusConstants.ADDRESS));
            node.setHostName(dataObj.getString(ChronusConstants.HOST_NAME));
            node.setCluster(currentNode.getCluster());
            node.setEnableExecutor(dataObj.getString(ChronusConstants.ENABLE_EXECUTOR));
            node.setEnableMaster(dataObj.getString(ChronusConstants.ENABLE_MASTER));
            node.setIsMaster(dataObj.getString(ChronusConstants.IS_MASTER));
            node.setState(dataObj.getString(ChronusConstants.STATE));
            node.setTag(dataObj.getString(ChronusConstants.TAG));
            node.setDataVersion(dataObj.getString(ChronusConstants.DATA_VERSION));
            node.setVersion(nodePath.split("\\$")[1]);
            return node;
        }
        return null;
    }

    @Override
    public boolean isMaster() throws Exception {
        return Objects.equals(currentNode.getAddress(), getCurrentMasterNodeAddress());
    }

    @Override
    public boolean isActiveMaster() throws Exception {
        return getCurrentMasterNodeAddress() != null;
    }

    @Override
    @Event(EventEnum.MASTER_ELECTED)
    public void nodeElectedMaster(String masterNodeAddress) throws Exception {
        List<String> nodePaths = zkManager.getChildren(rootPath);
        for (String tpath : nodePaths) {
            tpath = String.join("/", rootPath, tpath);
            byte[] data = zkManager.getData(tpath);
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                dataObj.put(ChronusConstants.IS_MASTER, ChronusConstants.N);
                zkManager.setData(tpath, JSONObject.toJSONString(dataObj).getBytes());
            }
        }

        for (String tpath : nodePaths) {
            if (tpath.startsWith(masterNodeAddress + "$")) {
                tpath = String.join("/", rootPath, tpath);
                byte[] data = zkManager.getData(tpath);
                if (data != null) {
                    JSONObject dataObj = JSONObject.parseObject(new String(data));
                    dataObj.put(ChronusConstants.IS_MASTER, ChronusConstants.Y);
                    zkManager.setData(tpath, JSONObject.toJSONString(dataObj).getBytes());
                    break;
                }
            }
        }
    }

    @Override
    public String getCurrentMasterNodeAddress() throws Exception {
        List<String> nodePaths = zkManager.getChildren(rootPath);
        for (String tpath : nodePaths) {
            byte[] data = zkManager.getData(String.join("/", rootPath, tpath));
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                if (ChronusConstants.Y.equals(dataObj.getString(ChronusConstants.ENABLE_MASTER))
                        && ChronusConstants.Y.equals(dataObj.getString(ChronusConstants.IS_MASTER))
                        && NodeStateEnum.isNormal(dataObj.getString(ChronusConstants.STATE))) {
                    return dataObj.getString(ChronusConstants.ADDRESS);
                }
            }
        }
        return null;
    }


    @Override
    public List<Node> getAllExecutorNode() throws Exception {
        List<Node> result = new ArrayList<>();
        List<String> nodePaths = zkManager.getChildren(rootPath);
        for (String tpath : nodePaths) {
            byte[] data = zkManager.getData(String.join("/", rootPath, tpath));
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                if (ChronusConstants.Y.equals(dataObj.getString(ChronusConstants.ENABLE_EXECUTOR))) {
                    Node node = new Node();
                    node.setAddress(dataObj.getString(ChronusConstants.ADDRESS));
                    node.setHostName(dataObj.getString(ChronusConstants.HOST_NAME));
                    node.setCluster(currentNode.getCluster());
                    node.setEnableExecutor(dataObj.getString(ChronusConstants.ENABLE_EXECUTOR));
                    node.setEnableMaster(dataObj.getString(ChronusConstants.ENABLE_MASTER));
                    node.setIsMaster(dataObj.getString(ChronusConstants.IS_MASTER));
                    node.setState(dataObj.getString(ChronusConstants.STATE));
                    node.setTag(dataObj.getString(ChronusConstants.TAG));
                    node.setDataVersion(dataObj.getString(ChronusConstants.DATA_VERSION));
                    node.setVersion(tpath.split("\\$")[1]);
                    result.add(node);
                }
            }
        }
        return result;
    }

    @Override
    public List<Node> getAllNode(String cluster) throws Exception {
        String tmpRootPath = ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), cluster);
        List<Node> result = new ArrayList<>();

        List<String> nodePaths = zkManager.getChildren(tmpRootPath);
        for (String tpath : nodePaths) {
            byte[] data = zkManager.getData(String.join("/", tmpRootPath, tpath));
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                Node node = new Node();
                node.setHostName(dataObj.getString(ChronusConstants.HOST_NAME));
                node.setCluster(cluster);
                node.setEnableExecutor(dataObj.getString(ChronusConstants.ENABLE_EXECUTOR));
                node.setEnableMaster(dataObj.getString(ChronusConstants.ENABLE_MASTER));
                node.setIsMaster(dataObj.getString(ChronusConstants.IS_MASTER));
                node.setAddress(dataObj.getString(ChronusConstants.ADDRESS));
                node.setState(dataObj.getString(ChronusConstants.STATE));
                node.setTag(dataObj.getString(ChronusConstants.TAG));
                node.setDataVersion(dataObj.getString(ChronusConstants.DATA_VERSION));
                node.setVersion(tpath.split("\\$")[1]);
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public void setTag(Node node) throws Exception {
        String tmpRootPath = ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), node.getCluster());
        String tmpNodePath = ZookeeperUtil.getFullNodePath(tmpRootPath, node.getAddress(), node.getVersion());
        if (zkManager.checkExists(tmpNodePath)) {
            byte[] data = zkManager.getData(tmpNodePath);
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                dataObj.put(ChronusConstants.TAG, node.getTag());
                zkManager.setData(tmpNodePath, JSONObject.toJSONString(dataObj).getBytes());
            }
        }
    }

    @Override
    public void offlineNode(Node node) throws Exception {
        String tmpRootPath = ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), node.getCluster());
        String tmpNodePath = ZookeeperUtil.getFullNodePath(tmpRootPath, node.getAddress(), node.getVersion());

        if (zkManager.checkExists(tmpNodePath)) {
            byte[] data = zkManager.getData(tmpNodePath);
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                dataObj.put(ChronusConstants.STATE, NodeStateEnum.OFFLINE.getState());
                if (Objects.equals(ChronusConstants.Y, dataObj.getString(ChronusConstants.ENABLE_MASTER))) {
                    dataObj.put(ChronusConstants.IS_MASTER, ChronusConstants.N);
                }
                zkManager.setData(tmpNodePath, JSONObject.toJSONString(dataObj).getBytes());
            }
        }
    }

    @Override
    public void onlineNode(Node node) throws Exception {
        String tmpRootPath = ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), node.getCluster());
        String tmpNodePath = ZookeeperUtil.getFullNodePath(tmpRootPath, node.getAddress(), node.getVersion());

        if (zkManager.checkExists(tmpNodePath)) {
            byte[] data = zkManager.getData(tmpNodePath);
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                dataObj.put(ChronusConstants.STATE, NodeStateEnum.NORMAL.getState());
                dataObj.put(ChronusConstants.REGISTER_TIME, String.valueOf(System.currentTimeMillis()));
                dataObj.put(ChronusConstants.DATA_VERSION, String.valueOf(System.currentTimeMillis()));
                zkManager.setData(tmpNodePath, JSONObject.toJSONString(dataObj).getBytes());
            }
        }
    }

}
