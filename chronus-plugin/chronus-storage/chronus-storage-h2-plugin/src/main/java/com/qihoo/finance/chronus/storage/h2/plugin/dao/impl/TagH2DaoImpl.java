package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.tag.dao.TagDao;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;

import java.util.List;
import java.util.Map;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
public class TagH2DaoImpl implements TagDao {

    @Override
    public void insert(TagEntity tagEntity) {

    }

    @Override
    public TagEntity selectByTagName(String tag) {
        return null;
    }

    @Override
    public List<TagEntity> selectListAll() {
        return null;
    }

    @Override
    public void delete(String tagName) {

    }

    @Override
    public void update(TagEntity tagEntity) {

    }

    @Override
    public PageResult<TagEntity> findAllByPage(Integer page, Integer limit, Map<String, String> param) {
        return null;
    }
}
