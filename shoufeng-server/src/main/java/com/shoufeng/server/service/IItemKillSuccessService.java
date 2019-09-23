package com.shoufeng.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shoufeng.model.entity.ItemKillSuccessEntity;

/**
 * <p>
 * 秒杀成功订单表 服务类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
public interface IItemKillSuccessService extends IService<ItemKillSuccessEntity> {
    /**
     * 查询某个用户抢到某个商品数量
     * @param userId 用户ID
     * @param itemId 商品ID
     * @return 抢到商品数量
     */
    Integer countUserItemNum(Long userId, Long itemId);
}
