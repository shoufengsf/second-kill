package com.shoufeng.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shoufeng.model.dto.ItemKillInfoDto;
import com.shoufeng.model.entity.ItemKillEntity;

import java.util.List;

/**
 * <p>
 * 待秒杀商品表 服务类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
public interface IItemKillService extends IService<ItemKillEntity> {
    List<ItemKillInfoDto> findActiveItemKillList();

    ItemKillInfoDto findItemKillById(Long id);

    Boolean killItemBase(Long userId, Long itemId);

    Boolean killItemRedisLock(Long userId, Long itemId);

    Boolean killItemRedissonLock(Long userId, Long itemId);

    Boolean killItemZKLock(Long userId, Long itemId);
}
