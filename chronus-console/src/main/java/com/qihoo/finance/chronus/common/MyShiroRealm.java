package com.qihoo.finance.chronus.common;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Objects;

public class MyShiroRealm extends AuthorizingRealm {

    //角色权限和对应权限添加
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        String name = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        if (Objects.equals("guest", name)) {
            simpleAuthorizationInfo.addRole("guest");
        } else if (Objects.equals("admin", name)) {
            simpleAuthorizationInfo.addRole("admin");
        } else {
            throw new UnknownAccountException("账户不存在!");
        }
        return simpleAuthorizationInfo;
    }

    //用户认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken atoken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) atoken;
        String name = token.getUsername();
        if (name == null) {
            return null;
        }
        if (!Objects.equals("guest", name) && !Objects.equals("admin", name)) {
            throw new UnknownAccountException("账户不存在!");
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(name, name, getName());
        return simpleAuthenticationInfo;
    }
}