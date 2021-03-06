package com.shoufeng.server.controller;


import com.shoufeng.server.common.pojo.Result;
import com.shoufeng.server.common.utils.JWTUtil;
import com.shoufeng.server.service.IUserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

//import org.apache.shiro.authz.annotation.RequiresRoles;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JWTUtil jwtUtil;

    @Value("${jwt.header}")
    private String header;

    @Autowired
    private IUserService iUserService;

    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse httpServletResponse) {
        if (iUserService.checkUsernamePassword(username, password)) {
            String passwordMd5 = DigestUtils.md5Hex(password);
            String sign = jwtUtil.sign(username, passwordMd5);
            httpServletResponse.setHeader(header, sign);
            return Result.ok("登陆成功", sign);
        } else {
            return Result.error("账号密码错误", null);
        }
    }

    @RequestMapping("/register")
//    @RequiresRoles(value = {"admin"})
    public Result registerUser(String username, String password, String phone, String email) {
        if (iUserService.registerUser(username, password, phone, email)) {
            return Result.ok("注册成功", null);
        } else {
            return Result.error("注册失败", null);
        }
    }
}
