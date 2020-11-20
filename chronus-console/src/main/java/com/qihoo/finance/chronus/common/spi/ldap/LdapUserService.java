package com.qihoo.finance.chronus.common.spi.ldap;

import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.user.dao.UserDao;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Date;

/**
 * @author chenxiyu
 * @date 2019/12/11
 */
@Slf4j
public class LdapUserService implements UserService {

    @Autowired
    private LdapProperties ldapProperties;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private UserDao userDao;

    @Override
    public UserEntity findByUserNo(String username, String password) {
        UserEntity userEntity = null;
        try{
            LdapQuery ldapQuery = LdapQueryBuilder.query().base(ldapProperties.getBase()).searchScope(SearchScope.SUBTREE)
                    .filter("(sAMAccountName={0})", username);

            boolean authResult = ldapTemplate.authenticate(ldapQuery.base(), ldapQuery.filter().encode(), password);
            if (!authResult) {
                log.debug("ldap authentication for {} failed", username);
                return null;
            }
            userEntity = ldapTemplate.lookup(username,new PersonAttributesMapper());
            //查询权限
            UserEntity user = userDao.selectUserInfoByUserNo(userEntity.getUserNo());
            if(user==null){
                userEntity.setState("Y");
                Date date = new Date();
                userEntity.setDateCreated(date);
                userEntity.setDateUpdated(date);
                userDao.insert(userEntity);
            }
        }catch (Exception e){
            log.error("ldap authentication for failed,{}",e.getMessage());
        }
        return  userEntity;
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
        return null;
    }


    private class PersonAttributesMapper implements AttributesMapper<UserEntity> {

        @Override
        public UserEntity mapFromAttributes(Attributes attributes) throws NamingException {
            UserEntity UserEntity = new UserEntity();
            UserEntity.setUserNo((String)attributes.get("cn").get());
            UserEntity.setName((String)attributes.get("sn").get());
            UserEntity.setEmail((String)attributes.get("email").get());
            return UserEntity;
        }
    }
}
