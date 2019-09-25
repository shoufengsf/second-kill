package com.shoufeng.server.configure.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shoufeng.model.entity.UserEntity;
import com.shoufeng.server.common.utils.JWTUtil;
import com.shoufeng.server.service.IUserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shoufeng
 */
@Service
public class MyRealm extends AuthorizingRealm {

    private static final Logger LOGGER = LogManager.getLogger(MyRealm.class);

    @Autowired
    private JWTUtil jwtUtil;

    private IUserService iUserService;

    @Autowired
    public void setUserService(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    /**
     * 判断token类型
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = jwtUtil.getUsername(principals.toString());
        QueryWrapper<UserEntity> userEntityWrapper = new QueryWrapper<>();
        userEntityWrapper.eq("user_name", username);
        UserEntity userEntity = iUserService.getOne(userEntityWrapper);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole(userEntity.getRole());
        Set<String> permission = new HashSet<>(Arrays.asList(userEntity.getPermission().split(",")));
        simpleAuthorizationInfo.addStringPermissions(permission);
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = jwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token invalid");
        }
        QueryWrapper<UserEntity> userEntityWrapper = new QueryWrapper<>();
        userEntityWrapper.eq("user_name", username);
        UserEntity userEntity = iUserService.getOne(userEntityWrapper);
        if (userEntity == null) {
            throw new AuthenticationException("User didn't existed!");
        }

        if (!jwtUtil.verify(token, username, userEntity.getPassword())) {
            throw new AuthenticationException("Username or password error or token expire");
        }

        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }
}
