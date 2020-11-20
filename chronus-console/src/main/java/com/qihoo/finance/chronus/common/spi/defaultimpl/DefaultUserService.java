package com.qihoo.finance.chronus.common.spi.defaultimpl;

import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.common.util.Md5Utils;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.user.dao.UserDao;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chenxiyu
 * @date 2019/12/11
 */
public class DefaultUserService  implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserEntity findByUserNo(String userNo, String password) {
        return userDao.selectUserInfoByUserNo(userNo,Md5Utils.getMD5(password.getBytes()));
    }

    @Override
    public void insert(UserEntity userEntity) {
        if (userDao.selectUserInfoByUserNo(userEntity.getUserNo()) != null) {
            throw new RuntimeException("用户:" + userEntity.getUserNo() + ",已经存在,无法创建!");
        }
        userDao.insert(userEntity);
    }

    @Override
    public void update(UserEntity userEntity) {
        userDao.update(userEntity);
    }

    @Override
    public void delete(String userNo) {
        userDao.delete(userNo);
    }

    @Override
    public PageResult selectListByPage(UserEntity userEntity) {
        return userDao.selectListByPage(userEntity.getPageNum(),userEntity.getPageSize(),userEntity);
    }

    @Override
    public UserEntity findByUserNo(String userNo) {
        //判断是否默认用户
        for (UserEntity userEntity: SecureUtils.userEntityList) {
            if(StringUtils.equals(userEntity.getUserNo(),userNo)){
                return userEntity;
            }
        }
        return userDao.selectUserInfoByUserNo(userNo);
    }


}
