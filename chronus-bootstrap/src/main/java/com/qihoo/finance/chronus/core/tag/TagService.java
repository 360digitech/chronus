package com.qihoo.finance.chronus.core.tag;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/8/14.
 */
public interface TagService {
    void insert(TagEntity tagEntity);

    void delete(String tagName);

    void update(TagEntity tagEntity);

    List<TagEntity> selectListAll();

    PageResult<TagEntity> selectListByPage(TagEntity tagEntity);
}
