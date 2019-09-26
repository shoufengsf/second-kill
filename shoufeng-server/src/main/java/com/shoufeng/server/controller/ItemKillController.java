package com.shoufeng.server.controller;


import com.shoufeng.model.dto.ItemKillInfoDto;
import com.shoufeng.server.common.pojo.Result;
import com.shoufeng.server.service.IItemKillService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//import org.apache.shiro.authz.annotation.RequiresAuthentication;
//import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * <p>
 * 待秒杀商品表 前端控制器
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@RestController
@RequestMapping("/goods/kill")
public class ItemKillController {

    @Autowired
    private IItemKillService iItemKillService;

    /**
     * 获取待秒杀商品
     *
     * @return 查询结果
     */
    @GetMapping("/list")
//    @RequiresAuthentication
    public Result listKillGoods() {
        List<ItemKillInfoDto> itemKillInfoList = iItemKillService.findActiveItemKillList();
        return Result.ok(null, itemKillInfoList);
    }

    @PostMapping("/execute")
//    @RequiresPermissions(value = {"kill"})
    public Result executeKill(@Param("userId") Long userId, @Param("itemId") Long itemId) {
//        Boolean flag = iItemKillService.killItemBase(userId, itemId);
//        Boolean flag = iItemKillService.killItemRedisLock(userId, itemId);
//        Boolean flag = iItemKillService.killItemRedissonLock(userId, itemId);
        Boolean flag = iItemKillService.killItemZKLock(userId, itemId);
        flag = flag == null ? false : flag;
        return flag ? Result.ok("秒杀成功", null) : Result.error("秒杀失败", null);
    }
}
