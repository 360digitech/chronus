package com.qihoo.finance.chronus.metadata.api.tag.dao;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface TagDao {

    void insert(TagEntity tagEntity);

    TagEntity selectByTagName(String tag);

    List<TagEntity> selectListAll();

    List<TagEntity> selectListByNames(Set<String> allTagNames);

    void delete(String tagName);

    void update(TagEntity tagEntity);

    PageResult<TagEntity> findAllByPage(Integer page, Integer limit, Map<String, String> param);
}
