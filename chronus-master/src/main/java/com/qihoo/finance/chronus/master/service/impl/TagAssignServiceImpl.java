package com.qihoo.finance.chronus.master.service.impl;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.core.tag.service.TagService;
import com.qihoo.finance.chronus.master.service.TagAssignService;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/10/31.
 */
@Slf4j
@Service("tagAssignService")
public class TagAssignServiceImpl implements TagAssignService {

    @Resource
    private TagService tagService;

    @Resource
    private NamingService namingService;

    @Override
    public Map<String, List<Node>> tagAssign(List<Node> executorNodeList, Set<String> allTagNames) throws Exception {
        List<TagEntity> tagEntityList = tagService.selectListByNames(allTagNames);

        Set<String> allTags = getAllTags(tagEntityList, executorNodeList);
        Map<String, String> hisTagExecutorMapping = executorNodeList.stream().collect(Collectors.toMap(e -> e.getAddress(), e -> e.getTag()));

        // 按tag分配执行机
        Map<String, List<Node>> tagExecutorMapping = getTagExecutorMapping(allTags, executorNodeList);

        for (Map.Entry<String, List<Node>> tagExecutorMappingEntry : tagExecutorMapping.entrySet()) {
            for (Node node : tagExecutorMappingEntry.getValue()) {
                if (!Objects.equals(node.getTag(), hisTagExecutorMapping.get(node.getAddress()))) {
                    try {
                        log.info("Node:{} tag发生变更 old:{} new:{}", node.getAddress(), hisTagExecutorMapping.get(node.getAddress()), node.getTag());
                        namingService.setTag(node);
                    } catch (Exception e) {
                        log.error("更新节点Tag异常 node:{} tag:{}", node.getAddress(), node.getTag());
                    }
                }
            }
        }
        return tagExecutorMapping;
    }

    private Set<String> getAllTags(List<TagEntity> tagEntityList, List<Node> executorNodeList) {
        Set<String> allTags = tagEntityList.stream().map(TagEntity::getTag).collect(Collectors.toCollection(LinkedHashSet::new));
        if (CollectionUtils.isNotEmpty(allTags)) {
            if (allTags.size() > executorNodeList.size()) {
                log.error("指定的tag数,超过了执行机的数量! allTags:{} executorNodeList:{}", allTags.size(), executorNodeList.size());
            }
        }
        allTags.remove(ChronusConstants.DEF_TAG);
        allTags.add(ChronusConstants.DEF_TAG);
        return allTags;
    }


    private Map<String, List<Node>> getTagExecutorMapping(Set<String> allTags, List<Node> executorNodeList) {
        Map<String, List<Node>> tagExecutorMapping = executorNodeList.stream().collect(Collectors.groupingBy(e -> e.getTag()));
        List<Node> defNodeList = tagExecutorMapping.getOrDefault(ChronusConstants.DEF_TAG, new ArrayList<>());

        for (Node node : executorNodeList) {
            // tag 被移除? 原tag 所属的机器放入def组
            if (!allTags.contains(node.getTag())) {
                node.setTag(ChronusConstants.DEF_TAG);
                defNodeList.add(node);
            }
        }

        defNodeList.sort(Comparator.comparing(e -> e.getVersion()));
        for (String tag : allTags) {
            if (ChronusConstants.DEF_TAG.equals(tag)) {
                continue;
            }
            TagEntity tagEntity = tagService.selectByTagName(tag);
            if (tagEntity == null) {
                log.error("未找到对应的Tag配置 tag:{}", tag);
                tagExecutorMapping.put(tag, new ArrayList<>());
                continue;
            }

            List<Node> hisNodeList = tagExecutorMapping.getOrDefault(tag, new ArrayList<>());
            if (hisNodeList.size() == tagEntity.getExecutorNum()) {
                continue;
            }
            if (hisNodeList.size() < tagEntity.getExecutorNum()) {
                int needAddCount = tagEntity.getExecutorNum() - hisNodeList.size();
                if (defNodeList.size() < needAddCount + 1) {
                    log.error("Tag:{} executorNum:{} poolSize:{} needAddCount:{} 需要执行机数量不够,请扩容!", tag, tagEntity.getExecutorNum(), defNodeList.size(), needAddCount);
                }
                needAddCount = defNodeList.size() >= needAddCount ? needAddCount : defNodeList.size() - 1;
                if (needAddCount > 0) {
                    // 从最后取节点
                    List<Node> addNode = new ArrayList<>(defNodeList.subList(defNodeList.size() - needAddCount, defNodeList.size()));
                    for (Node node : addNode) {
                        node.setTag(tag);
                    }
                    defNodeList.removeAll(addNode);
                    hisNodeList.addAll(addNode);
                    tagExecutorMapping.put(tag, hisNodeList);
                }
            } else {
                hisNodeList.sort(Comparator.comparing(e -> e.getVersion()));
                int fromIndex = tagEntity.getExecutorNum();
                int toIndex = hisNodeList.size();
                List<Node> removeNode = new ArrayList<>(hisNodeList.subList(fromIndex, toIndex));
                for (Node node : removeNode) {
                    node.setTag(ChronusConstants.DEF_TAG);
                }
                hisNodeList.removeAll(removeNode);
                defNodeList.addAll(removeNode);
                tagExecutorMapping.put(tag, hisNodeList);
            }
        }
        tagExecutorMapping.put(ChronusConstants.DEF_TAG, defNodeList);
        return tagExecutorMapping;
    }

}
