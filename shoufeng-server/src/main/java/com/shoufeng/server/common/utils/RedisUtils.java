package com.shoufeng.server.common.utils;

import com.alibaba.fastjson.JSON;
import com.shoufeng.server.common.constant.DirectionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author shoufeng
 */
@Component
public class RedisUtils {

    //过期时间一天
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    //不过期
    public final static long NOT_EXPIRE = -1;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, String> valueOperations;
    private final HashOperations<String, String, String> hashOperations;
    private final ListOperations<String, String> listOperations;
    private final SetOperations<String, String> setOperations;
    private final ZSetOperations<String, String> zSetOperations;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate, ValueOperations<String, String> valueOperations, HashOperations<String, String, String> hashOperations, ListOperations<String, String> listOperations, SetOperations<String, String> setOperations, ZSetOperations<String, String> zSetOperations) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = valueOperations;
        this.hashOperations = hashOperations;
        this.listOperations = listOperations;
        this.setOperations = setOperations;
        this.zSetOperations = zSetOperations;
    }

    /**
     * 添加对象（为空或小于等于0则永不过期）
     *
     * @param key
     * @param value
     * @param expire
     */
    public void set(String key, Object value, Long expire) {
        if (expire == null || expire <= 0) {
            expire = NOT_EXPIRE;
        }
        valueOperations.set(key, JSON.toJSONString(value), expire, TimeUnit.SECONDS);
    }

    /**
     * 添加对象（默认一天）
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    /**
     * 将对象设置过期时间
     *
     * @param key
     * @param expire
     */
    public void expire(String key, Long expire) {
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 获取对象
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        String value = valueOperations.get(key);
        return value == null ? null : JSON.parseObject(value, clazz);
    }

    /**
     * 删除数据
     *
     * @param key
     * @return
     */
    public Long delete(String... key) {
        List<String> keyList = Arrays.asList(key);
        return redisTemplate.delete(keyList);
    }

    /**
     * 添加hashmap
     *
     * @param key
     * @param stringObjectMap
     */
    public void hashPutAll(String key, Map<String, ?> stringObjectMap) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringObjectMap.forEach((s, o) -> {
            stringStringMap.put(s, JSON.toJSONString(o));
        });
        hashOperations.putAll(key, stringStringMap);
    }

    /**
     * 添加单个hash
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hashPut(String key, String hashKey, Object value) {
        hashOperations.put(key, hashKey, JSON.toJSONString(value));
    }

    /**
     * 获取指定hash中的指定对象
     *
     * @param key
     * @param hashKey
     * @param Clazz
     * @param <T>
     * @return
     */
    public <T> T hashGet(String key, String hashKey, Class<T> Clazz) {
        String value = hashOperations.get(key, hashKey);
        return value == null ? null : JSON.parseObject(value, Clazz);
    }

    /**
     * list添加集合
     *
     * @param key
     * @param directionEnum
     * @param value
     */
    public void listPushAll(String key, DirectionEnum directionEnum, Object... value) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < value.length; i++) {
            list.add(JSON.toJSONString(value));
        }
        switch (directionEnum) {
            case LEFT: {
                listOperations.leftPushAll(key, list);
                return;
            }
            case RIGHT: {
                listOperations.rightPushAll(key, list);
            }
        }
    }

    /**
     * list单个添加
     *
     * @param key
     * @param directionEnum
     * @param value
     * @return
     */
    public Long listPush(String key, DirectionEnum directionEnum, Object value) {
        switch (directionEnum) {
            case LEFT: {
                return listOperations.leftPush(key, JSON.toJSONString(value));
            }
            case RIGHT: {
                return listOperations.rightPush(key, JSON.toJSONString(value));
            }
        }
        return null;
    }

    /**
     * list出列
     *
     * @param key
     * @param directionEnum
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T listPop(String key, DirectionEnum directionEnum, Class<T> clazz) {
        String value = null;
        switch (directionEnum) {
            case LEFT: {
                value = listOperations.leftPop(key);
            }
            case RIGHT: {
                value = listOperations.rightPop(key);
            }
        }
        return StringUtils.isBlank(value) ? null : JSON.parseObject(value, clazz);
    }

    /**
     * list超时出列
     *
     * @param key
     * @param directionEnum
     * @param clazz
     * @param timeout
     * @param <T>
     * @return
     */
    public <T> T listPop(String key, DirectionEnum directionEnum, Class<T> clazz, Long timeout) {
        String value = null;
        switch (directionEnum) {
            case LEFT: {
                value = listOperations.leftPop(key, timeout, TimeUnit.SECONDS);
            }
            case RIGHT: {
                value = listOperations.rightPop(key, timeout, TimeUnit.SECONDS);
            }
        }
        return StringUtils.isBlank(value) ? null : JSON.parseObject(value, clazz);
    }
}
