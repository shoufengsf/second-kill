package com.shoufeng.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shoufeng.model.dto.ItemKillInfo;
import com.shoufeng.model.entity.ItemKill;

import java.util.List;

/**
 * <p>
 * 待秒杀商品表 服务类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
public interface IItemKillService extends IService<ItemKill> {
    List<ItemKillInfo> findActiveItemKillList();
    ItemKillInfo findItemKillById(Long id);
    Boolean killItem(Long userId, Long itemId);
}
