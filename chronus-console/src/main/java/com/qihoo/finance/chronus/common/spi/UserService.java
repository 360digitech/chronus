package com.qihoo.finance.chronus.common.spi;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;

import java.util.List;

/**
 * @author chenxiyu
 * @date 2019/12/7
 */
public interface UserService {

    UserEntity findByUserNo(String userNo, String password);

    void insert(UserEntity userEntity);

    void update(UserEntity userEntity);

    void delete(String userNo);

    PageResult selectListByPage(UserEntity userEntity);

    UserEntity findByUserNo(String userNo);
}
