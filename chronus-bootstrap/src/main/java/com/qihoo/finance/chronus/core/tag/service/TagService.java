package com.qihoo.finance.chronus.core.tag.service;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;

import java.util.List;
import java.util.Set;

/**
 * Created by xiongpu on 2019/8/14.
 */
public interface TagService {
    void insert(TagEntity tagEntity);
    void delete(String tagName);
    void update(TagEntity tagEntity) throws Exception;
    TagEntity selectByTagName(String tagName);
    List<TagEntity> selectListAll();
    List<TagEntity> selectListByNames(Set<String> allTagNames);
    PageResult<TagEntity> selectListByPage(TagEntity tagEntity);
}
