package com.qihoo.finance.chronus.registry.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.log.annotation.NodeLog;
import com.qihoo.finance.chronus.core.log.enums.NodeLogTypeEnum;
import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.util.InstanceUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Slf4j
public class MasterElectionServiceNacosImpl implements MasterElectionService {

    @NacosInjected
    private NamingService nacosNamingService;

    @Resource
    private NodeInfo currentNode;

    private boolean masterGroupByTag;
    /* */
    //@NacosInjected
    //https://github.com/nacos-group/nacos-spring-project/issues/144
    @Resource
    private NamingMaintainService namingMaintainService;
    /* */

    @Override
    public void setMasterGroupByTag(boolean masterGroupByTag) {
        this.masterGroupByTag = masterGroupByTag;
    }

    @Override
    @NodeLog(value = NodeLogTypeEnum.MASTER_ELECTION, resultPutContent = true)
    public String election(String currentNodeAddress) throws Exception {
        List<String> clusters = new ArrayList<>(1);
        clusters.add(currentNode.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);

        Instance instance = instanceList.stream().filter(e -> e.isEnabled() && e.isHealthy()
                && e.getMetadata().containsKey(ChronusConstants.REGISTER_TIME)
                && e.getMetadata().containsKey(ChronusConstants.ENABLE_MASTER) && Objects.equals(ChronusConstants.Y, e.getMetadata().get(ChronusConstants.ENABLE_MASTER))
                && isCurrentTagNode(e))
                .sorted(Comparator.comparing(e -> e.getMetadata().get(ChronusConstants.REGISTER_TIME))).findFirst().orElse(null);
        //使用注册时间筛选 存在时钟回拨导致选举结果不一致问题
        String electionResult = instance != null ? InstanceUtils.getAddressByInstance(instance) : null;
        log.info("MASTER_ELECTION result:{}", electionResult);
        if (Objects.equals(currentNodeAddress, electionResult)) {
            nodeElectedMaster(electionResult);
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
        List<String> clusters = new ArrayList<>(1);
        clusters.add(currentNode.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);
        for (Instance instance : instanceList) {
            if (!masterNodeAddress.equals(InstanceUtils.getAddressByInstance(instance)) && isCurrentTagNode(instance)) {
                instance.getMetadata().put(ChronusConstants.IS_MASTER, ChronusConstants.N);
                namingMaintainService.updateInstance(ChronusConstants.NODE_NAME_CHRONUS, instance);
            }
        }
        for (Instance instance : instanceList) {
            if (masterNodeAddress.equals(InstanceUtils.getAddressByInstance(instance))) {
                instance.getMetadata().put(ChronusConstants.IS_MASTER, ChronusConstants.Y);
                namingMaintainService.updateInstance(ChronusConstants.NODE_NAME_CHRONUS, instance);
                break;
            }
        }
    }

    @Override
    public String getCurrentMasterNodeAddress() throws Exception {
        List<String> clusters = new ArrayList<>(1);
        clusters.add(currentNode.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);

        Instance instance = instanceList.stream().filter(e -> e.isEnabled() && e.isHealthy()
                && e.getMetadata().containsKey(ChronusConstants.IS_MASTER)
                && ChronusConstants.Y.equals(e.getMetadata().get(ChronusConstants.IS_MASTER))
                && isCurrentTagNode(e)).findFirst().orElse(null);
        return instance != null ? InstanceUtils.getAddressByInstance(instance) : null;
    }

    /**
     * 节点筛选 筛选实例是否和当前节点为同一个tag
     * 该判断受masterGroupByTag属性影响
     *
     * @param instance
     * @return
     */
    private boolean isCurrentTagNode(Instance instance) {
        if (!this.masterGroupByTag) {
            return true;
        }
        return Objects.equals(currentNode.getTag(), instance.getMetadata().get(ChronusConstants.TAG));
    }
}
