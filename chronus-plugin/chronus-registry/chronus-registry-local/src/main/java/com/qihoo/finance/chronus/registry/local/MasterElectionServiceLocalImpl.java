package com.qihoo.finance.chronus.registry.local;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.log.annotation.NodeLog;
import com.qihoo.finance.chronus.core.log.enums.NodeLogTypeEnum;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.api.Node;
import com.qihoo.finance.chronus.registry.util.LocalRegistryContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Slf4j
public class MasterElectionServiceLocalImpl implements MasterElectionService {

    private boolean masterGroupByTag;

    @Resource
    private NodeInfo currentNode;

    @Override
    public void setMasterGroupByTag(boolean masterGroupByTag) {
        this.masterGroupByTag = masterGroupByTag;
    }

    @Override
    @NodeLog(value = NodeLogTypeEnum.MASTER_ELECTION, resultPutContent = true)
    public String election(String currentNodeAddress) throws Exception {
        nodeElectedMaster(currentNodeAddress);
        return LocalRegistryContext.getNode().getAddress();
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
        LocalRegistryContext.getNode().setIsMaster(ChronusConstants.Y);
    }

    @Override
    public String getCurrentMasterNodeAddress() throws Exception {
        Node node = LocalRegistryContext.getNode();
        if (node != null) {
            if (ChronusConstants.Y.equals(node.getEnableMaster()) && ChronusConstants.Y.equals(node.getIsMaster()) && NodeStateEnum.isNormal(node.getState())) {
                return node.getAddress();
            }
        }
        return null;
    }
}
