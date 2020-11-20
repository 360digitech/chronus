package com.qihoo.finance.chronus.metadata.api.user.dao;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;

/**
 * @author chenxiyu
 * @date 2019/12/17
 */
public interface UserDao {
    void insert(UserEntity userEntity);

    void update(UserEntity userEntity);

    void delete(String userNo);

    UserEntity selectUserInfoByUserNo(String userNo,String password);

    UserEntity selectUserInfoByUserNo(String userNo);

    PageResult<UserEntity> selectListByPage(Integer page, Integer limit, UserEntity userEntity);

}
