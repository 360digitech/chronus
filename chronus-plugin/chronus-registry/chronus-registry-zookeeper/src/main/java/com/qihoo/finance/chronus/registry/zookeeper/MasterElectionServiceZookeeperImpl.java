package com.qihoo.finance.chronus.registry.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.log.annotation.NodeLog;
import com.qihoo.finance.chronus.core.log.enums.NodeLogTypeEnum;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.util.ZookeeperManager;
import com.qihoo.finance.chronus.registry.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.api.transaction.CuratorOp;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Slf4j
public class MasterElectionServiceZookeeperImpl implements MasterElectionService {

    private boolean masterGroupByTag;

    @Resource
    private NodeInfo currentNode;

    @Resource
    protected ZookeeperManager zkManager;

    @Override
    public void setMasterGroupByTag(boolean masterGroupByTag) {
        this.masterGroupByTag = masterGroupByTag;
    }

    @Override
    @NodeLog(value = NodeLogTypeEnum.MASTER_ELECTION, resultPutContent = true)
    public String election(String currentNodeAddress) throws Exception {
        String tmpRootPath = getRootPath();
        List<String> nodeList = zkManager.getChildren(tmpRootPath);

        List<String> normalPathList = new ArrayList<>();
        for (String tpath : nodeList) {
            byte[] data = zkManager.getData(String.join("/", tmpRootPath, tpath));
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                if (dataObj.containsKey(ChronusConstants.STATE) && NodeStateEnum.isNormal(dataObj.getString(ChronusConstants.STATE))
                        && Objects.equals(ChronusConstants.Y, dataObj.getString(ChronusConstants.ENABLE_MASTER))
                        && isCurrentTagNode(dataObj)) {
                    normalPathList.add(tpath);
                }
            }
        }
        String electionResult = null;
        if (CollectionUtils.isNotEmpty(normalPathList)) {
            String tpath = normalPathList.stream().min(Comparator.comparing(e -> Long.valueOf(e.substring(e.lastIndexOf("$") + 1)))).get();
            electionResult = tpath.substring(0, tpath.indexOf("$"));
            log.info("MASTER_ELECTION result:{}", electionResult);
            if (Objects.equals(currentNodeAddress, electionResult)) {
                nodeElectedMaster(electionResult);
            }
        }
        return electionResult;
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
    public void nodeElectedMaster(String masterNodeAddress) throws Exception {
        List<String> nodePaths = zkManager.getChildren(getRootPath());
        List<CuratorOp> curatorOpList = new ArrayList<>();
        for (String tpath : nodePaths) {
            String nodePath = String.join("/", getRootPath(), tpath);
            byte[] data = zkManager.getData(nodePath);
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                if (isCurrentTagNode(dataObj) && !tpath.startsWith(masterNodeAddress + "$")) {
                    dataObj.put(ChronusConstants.IS_MASTER, ChronusConstants.N);
                    //zkManager.setData(nodePath, JSONObject.toJSONString(dataObj).getBytes());
                    curatorOpList.add(zkManager.createSetDataOp(nodePath, JSONObject.toJSONString(dataObj)));
                }
            }
        }

        for (String tpath : nodePaths) {
            if (tpath.startsWith(masterNodeAddress + "$")) {
                tpath = String.join("/", getRootPath(), tpath);
                byte[] data = zkManager.getData(tpath);
                if (data != null) {
                    JSONObject dataObj = JSONObject.parseObject(new String(data));
                    dataObj.put(ChronusConstants.IS_MASTER, ChronusConstants.Y);
                    //zkManager.setData(tpath, JSONObject.toJSONString(dataObj).getBytes());
                    curatorOpList.add(zkManager.createSetDataOp(tpath, JSONObject.toJSONString(dataObj)));
                    break;
                }
            }
        }
        zkManager.forOperations(curatorOpList);
    }

    @Override
    public String getCurrentMasterNodeAddress() throws Exception {
        List<String> nodePaths = zkManager.getChildren(getRootPath());
        for (String tpath : nodePaths) {
            byte[] data = zkManager.getData(String.join("/", getRootPath(), tpath));
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                if (ChronusConstants.Y.equals(dataObj.getString(ChronusConstants.ENABLE_MASTER))
                        && ChronusConstants.Y.equals(dataObj.getString(ChronusConstants.IS_MASTER))
                        && NodeStateEnum.isNormal(dataObj.getString(ChronusConstants.STATE))
                        && isCurrentTagNode(dataObj)) {
                    return dataObj.getString(ChronusConstants.ADDRESS);
                }
            }
        }
        return null;
    }

    private String getRootPath() {
        return ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), currentNode.getCluster());
    }

    /**
     * 节点筛选 筛选实例是否和当前节点为同一个tag
     * 该判断受masterGroupByTag属性影响
     *
     * @param instance
     * @return
     */
    private boolean isCurrentTagNode(JSONObject instance) {
        if (!this.masterGroupByTag) {
            return true;
        }
        return Objects.equals(currentNode.getTag(), instance.getString(ChronusConstants.TAG));
    }
}
