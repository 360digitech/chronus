package com.qihoo.finance.chronus.core.tag.impl;

import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.core.tag.TagService;
import com.qihoo.finance.chronus.core.task.TaskService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.tag.dao.TagDao;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiongpu on 2019/8/14.
 */
@Service("tagService")
public class TagServiceImpl implements TagService {

    @Resource
    private TagDao tagDao;

    @Resource
    private TaskService taskService;

    @Override
    public void insert(TagEntity tagEntity) {
        if (tagDao.selectByTagName(tagEntity.getTag()) != null) {
            throw new RuntimeException("Tag:" + tagEntity.getTag() + ",已经存在,无法创建!");
        }
        tagEntity.setId(null);
        tagEntity.setDateCreated(DateUtils.now());
        tagEntity.setDateUpdated(DateUtils.now());
        tagDao.insert(tagEntity);
    }

    @Override
    public void delete(String tagName) {
        Set<String> tagNameSet = taskService.existTaskByTag(tagName);
        if (CollectionUtils.isNotEmpty(tagNameSet)) {
            throw new RuntimeException("Tag:" + tagName + ",还存在关联任务,无法删除!");
        }
        tagDao.delete(tagName);
    }

    @Override
    public void update(TagEntity tagEntity) {
        tagDao.update(tagEntity);
    }

    @Override
    public List<TagEntity> selectListAll() {
        return tagDao.selectListAll();
    }

    @Override
    public PageResult<TagEntity> selectListByPage(TagEntity tagEntity) {
        Map<String, String> param = new HashMap<>();
        if (StringUtils.isNotBlank(tagEntity.getRemark())) {
            param.put("remark", tagEntity.getRemark());
        }
        if (StringUtils.isNotBlank(tagEntity.getTag())) {
            param.put("tag", tagEntity.getTag());
        }
        return tagDao.findAllByPage(tagEntity.getPageNum(), tagEntity.getPageSize(), param);
    }
}
