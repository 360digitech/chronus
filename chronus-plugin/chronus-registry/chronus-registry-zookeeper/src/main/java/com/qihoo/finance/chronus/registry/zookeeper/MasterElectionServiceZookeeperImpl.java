package com.qihoo.finance.chronus.registry.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.event.annotation.Event;
import com.qihoo.finance.chronus.core.event.enums.EventEnum;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.util.ZookeeperManager;
import com.qihoo.finance.chronus.registry.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

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

    @Resource
    private NodeInfo currentNode;

    @Resource
    protected ZookeeperManager zkManager;

    @Override
    @Event(value = EventEnum.MASTER_ELECTION, resultPutContent = true)
    public String election() throws Exception {
        String tmpRootPath = ZookeeperUtil.getFullRootPath(zkManager.getRootPath(), currentNode.getCluster());
        List<String> nodeList = zkManager.getChildren(tmpRootPath);

        List<String> normalPathList = new ArrayList<>();
        for (String tpath : nodeList) {
            byte[] data = zkManager.getData(String.join("/", tmpRootPath, tpath));
            if (data != null) {
                JSONObject dataObj = JSONObject.parseObject(new String(data));
                if (dataObj.containsKey(ChronusConstants.STATE) && NodeStateEnum.isNormal(dataObj.getString(ChronusConstants.STATE))
                        && Objects.equals(ChronusConstants.Y, dataObj.getString(ChronusConstants.ENABLE_MASTER))) {
                    normalPathList.add(tpath);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(normalPathList)) {
            String tpath = normalPathList.stream().min(Comparator.comparing(e -> Long.valueOf(e.substring(e.lastIndexOf("$") + 1)))).get();
            return tpath.substring(0, tpath.indexOf("$"));
        }

        return null;
    }
}
