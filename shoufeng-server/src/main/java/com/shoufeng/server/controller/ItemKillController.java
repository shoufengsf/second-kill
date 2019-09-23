package com.shoufeng.server.controller;


import com.shoufeng.model.dto.ItemKillInfo;
import com.shoufeng.server.common.pojo.Result;
import com.shoufeng.server.service.IItemKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Result listKillGoods() {
        List<ItemKillInfo> itemKillInfoList = iItemKillService.findActiveItemKillList();
        return Result.ok(null, itemKillInfoList);
    }
}
