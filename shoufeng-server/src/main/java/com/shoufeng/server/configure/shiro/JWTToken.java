package com.shoufeng.server.configure.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 自定义令牌用于替代UsernamePasswordToken
 *
 * @author shoufeng
 */
public class JWTToken implements AuthenticationToken {

    // 密钥
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
