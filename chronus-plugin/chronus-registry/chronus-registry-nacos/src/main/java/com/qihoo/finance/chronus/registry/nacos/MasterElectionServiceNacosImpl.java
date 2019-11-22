package com.qihoo.finance.chronus.registry.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.event.annotation.Event;
import com.qihoo.finance.chronus.core.event.enums.EventEnum;
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
    private NodeInfo currentNodeCluster;

    @Override
    @Event(value = EventEnum.MASTER_ELECTION, resultPutContent = true)
    public String election() throws Exception {
        List<String> clusters = new ArrayList<>(1);
        clusters.add(currentNodeCluster.getCluster());
        List<Instance> instanceList = nacosNamingService.getAllInstances(ChronusConstants.NODE_NAME_CHRONUS, clusters);

        Instance instance = instanceList.stream().filter(e -> e.isEnabled() && e.isHealthy()
                && e.getMetadata().containsKey(ChronusConstants.REGISTER_TIME)
                && e.getMetadata().containsKey(ChronusConstants.ENABLE_MASTER) && Objects.equals(ChronusConstants.Y, e.getMetadata().get(ChronusConstants.ENABLE_MASTER)))
                .sorted(Comparator.comparing(e -> e.getMetadata().get(ChronusConstants.REGISTER_TIME))).findFirst().orElse(null);
        return instance != null ? InstanceUtils.getAddressByInstance(instance) : null;
    }
}
