package com.qihoo.finance.chronus.common.util;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author chenxiyu
 * @date 2019/12/5
 */
public class SecureUtils {
    public final static List<UserEntity> userEntityList = new ArrayList<>(2);

    static{
        UserEntity admin = generateDefaultUser(ChronusConstants.ADMIN, ChronusConstants.ALL);
        UserEntity guest = generateDefaultUser(ChronusConstants.GUEST,ChronusConstants.PUBLIC);
        userEntityList.add(admin);
        userEntityList.add(guest);
    }


    private static UserEntity generateDefaultUser(String name, String group){
        UserEntity userEntity = new UserEntity();
        userEntity.setUserNo(name);
        userEntity.setPwd(name);
        userEntity.setName(name);
        userEntity.setRoleNo(name);
        userEntity.setGroup(group);
        return userEntity;
    }

    private static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static Object getPrincipal(){
        return getSubject().getPrincipal();
    }

    public static boolean isPermitted(String permission) {
        return getSubject().isPermitted(permission);
    }

    public static boolean[] isPermitted(String... permissions) {
        return getSubject().isPermitted(permissions);
    }

    public static boolean isPermittedAll(String... permissions) {
        return getSubject().isPermittedAll(permissions);
    }

    public static void checkPermission(String permission) throws AuthorizationException {
        getSubject().checkPermission(permission);
    }

    public static void checkPermissions(String... permissions) throws AuthorizationException {
        getSubject().checkPermissions(permissions);
    }

    public static boolean hasRole(String roleIdentifier) {
        return getSubject().hasRole(roleIdentifier);
    }

    public static boolean[] hasRoles(List<String> roleIdentifiers) {
        return getSubject().hasRoles(roleIdentifiers);
    }

    public static boolean hasAllRoles(Collection<String> roleIdentifiers) {
        return getSubject().hasAllRoles(roleIdentifiers);
    }

    public static void checkRole(String roleIdentifier) throws AuthorizationException {
        getSubject().checkRole(roleIdentifier);
    }

    public static void checkRoles(Collection<String> roleIdentifiers) throws AuthorizationException {
        getSubject().checkRoles(roleIdentifiers);
    }

    public static void checkRoles(String... roleIdentifiers) throws AuthorizationException {
        getSubject().checkRoles(roleIdentifiers);
    }

    public static boolean isAuthenticated() {
        return getSubject().isAuthenticated();
    }
}
