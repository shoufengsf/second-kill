package com.shoufeng.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoufeng.model.entity.UserEntity;
import com.shoufeng.model.mapper.UserMapper;
import com.shoufeng.server.service.IUserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    @Override
    public Boolean checkUsernamePassword(String username, String password) {
        QueryWrapper<UserEntity> userEntityQueryWrapper = new QueryWrapper<>();
        userEntityQueryWrapper.eq("user_name", username);
        userEntityQueryWrapper.eq("password", DigestUtils.md5Hex(password));
        UserEntity userEntity = this.getOne(userEntityQueryWrapper);
        return userEntity != null;
    }

    @Override
    public Boolean registerUser(String username, String password, String phone, String email) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(username);
        userEntity.setPassword(DigestUtils.md5Hex(password));
        userEntity.setPhone(phone);
        userEntity.setEmail(email);
        userEntity.setIsActive(1);
        userEntity.setRole("normal");
        userEntity.setPermission("permission1,permission2,permission3");
        return save(userEntity);
    }

}
