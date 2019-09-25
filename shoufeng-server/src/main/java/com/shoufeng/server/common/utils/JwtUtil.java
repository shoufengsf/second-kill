package com.shoufeng.server.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * jwt工具类
 *
 * @author shoufeng
 */

@Component
public class JWTUtil {

    private final static String TOKEN_PREFIX = "jwt_token_";
    private final static Logger LOGGER = LoggerFactory.getLogger(JWTUtil.class);
    // 过期时间5分钟
    private final static long EXPIRE_TIME = 1 * 60 * 1000;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${jwt.salt}")
    private String salt;

    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public boolean verify(String token, String username, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret + salt);
            JWTVerifier verifier = JWT.require(algorithm).
                    withClaim("username", username)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return checkRedisToken(token);
        } catch (TokenExpiredException e) {
            //只有当redis中也没有token时，才是真的过期
            if (checkRedisToken(token)) {
                return true;
            } else {
                LOGGER.error("token过期: ", e);
                return false;
            }
        } catch (Exception exception) {
            LOGGER.error("校验失败: ", exception);
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名,5min后过期
     *
     * @param username 用户名
     * @param secret   用户的密码
     * @return 加密的token
     */
    public String sign(String username, String secret) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret + salt);
        // 附带username信息
        String jwtToken = JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .sign(algorithm);
        redisUtil.set(TOKEN_PREFIX + jwtToken, jwtToken, EXPIRE_TIME / 1000);
        return jwtToken;
    }

    /**
     * 检查redis中是否存在该token，并刷新过期时间
     *
     * @param token
     * @return
     */
    private Boolean checkRedisToken(String token) {
        //如果redis里不为空则返回true
        if (redisUtil.get(TOKEN_PREFIX + token, String.class) != null) {
            redisUtil.expire(TOKEN_PREFIX + token, EXPIRE_TIME / 1000);
            return true;
        }
        return false;
    }
}
