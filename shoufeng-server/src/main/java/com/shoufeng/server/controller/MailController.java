package com.shoufeng.server.controller;


import com.shoufeng.server.service.SecondKillMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private SecondKillMailService secondKillMailService;

    @RequestMapping("/test")
    public void test() {
        secondKillMailService.sendSecondKillSuccessMail("915855132@qq.com", "守风", "李敖大全集");
    }

}
