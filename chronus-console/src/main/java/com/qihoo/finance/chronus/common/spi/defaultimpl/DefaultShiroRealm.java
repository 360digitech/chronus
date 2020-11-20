package com.qihoo.finance.chronus.common.spi.defaultimpl;

import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.common.util.SecureUtils;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author chenxiyu
 * @date 2019/12/11
 */
@Slf4j
public class DefaultShiroRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken atoken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) atoken;
        String name = token.getUsername();
        String password = new String((char[]) token.getCredentials());
        try {
            UserEntity user = userService.findByUserNo(name, password);
            if (null == user){
                user = isDefaultUser(name,password);
                if (null == user) {
                    throw new UnknownAccountException("Unknown account!");
                }
            }
            user.setToken(SecurityUtils.getSubject().getSession().getId().toString());
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUserNo(), token.getCredentials(), getName());
            return authenticationInfo;
        } catch (Exception e) {
            log.error("defaultShiro authentication failed", e.toString());
            return null;
        }
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String user = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(Collections.singleton(user));
        return simpleAuthorizationInfo;
    }

    /**
     * 试用用户默认给与账号
     */
    private UserEntity isDefaultUser(String userNo,String password) {
        for (UserEntity user: SecureUtils.userEntityList) {
            if(StringUtils.equals(user.getUserNo(),userNo)&&StringUtils.equals(user.getPwd(),password)){
                return user;
            }
        }
        return null;
    }


}