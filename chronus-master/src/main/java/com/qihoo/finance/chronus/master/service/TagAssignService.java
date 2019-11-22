package com.qihoo.finance.chronus.master.service;

import com.qihoo.finance.chronus.registry.api.Node;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiongpu on 2019/9/16.
 */
public interface TagAssignService {

    /**
     * tag-执行机分配
     *
     * @param executorNodeList
     * @param allTagNames
     * @return
     * @throws Exception
     */
    Map<String, List<Node>> tagAssign(List<Node> executorNodeList, Set<String> allTagNames) throws Exception;

}
