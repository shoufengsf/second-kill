package com.shoufeng.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shoufeng.model.entity.UserEntity;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
public interface IUserService extends IService<UserEntity> {

    /**
     * 校验账号密码
     *
     * @param username
     * @param password
     * @return
     */
    Boolean checkUsernamePassword(String username, String password);

    /**
     * 注册用户
     *
     * @param username
     * @param password
     * @return
     */
    Boolean registerUser(String username, String password, String phone, String email);
}
