package com.qihoo.finance.chronus.core.task.cache;

import com.qihoo.finance.chronus.common.ehcache.AbstractLocalCacheLoader;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.core.task.service.TaskService;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiongpu on 2016/7/26.
 */
@Slf4j
@Component
public class TaskCache extends AbstractLocalCacheLoader {
    private static final String CACHE_NAME = "CHRONUS_TASK_CACHE";

    private static TaskCache instance;

    @Resource
    private TaskService taskService;

    public static TaskEntity getTaskByClusterAndName(String cluster, String taskName) {
        if (instance == null) {
            synchronized (TaskCache.class) {
                if (instance == null) {
                    instance = SpringContextHolder.getBean(TaskCache.class);
                }
            }
        }
        TaskEntity cacheResult = instance.get(cluster + taskName);
        if (cacheResult == null) {
            // 因为信息一定来自数据库 当没有的时候 直接刷新缓存
            instance.update();
            cacheResult = instance.get(cluster + taskName);
        }
        return cacheResult;
    }

    @Override
    public Map<String, Object> init() {
        List<TaskEntity> taskEntityList = taskService.selectListAll();
        Map<String, Object> map = new HashMap<>(taskEntityList.size());
        for (TaskEntity taskEntity : taskEntityList) {
            map.put(taskEntity.getCluster() + taskEntity.getTaskName(), taskEntity);
        }
        return map;

    }

    @Override
    protected String getCacheName() {
        return CACHE_NAME;
    }
}
