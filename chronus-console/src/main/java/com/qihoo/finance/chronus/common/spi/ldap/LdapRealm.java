package com.qihoo.finance.chronus.common.spi.ldap;

import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;


/**
 * @author chenxiyu
 * @date 2019/12/7
 */
@Slf4j
public class LdapRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userNo = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        try {
            UserEntity user = userService.findByUserNo(userNo, password);
            if (user == null) {
                throw new UnknownAccountException("incorrect username or password");
            }
            user.setToken(SecurityUtils.getSubject().getSession().getId().toString());
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, token.getCredentials(), getName());
            return authenticationInfo;
        } catch (Exception e) {
            log.error("ldap authentication failed,{}", e.getMessage());
            return null;
        }

    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        UserEntity userEntity = (UserEntity) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(Collections.singleton(userEntity.getRoleNo()));
        return simpleAuthorizationInfo;

    }


}

