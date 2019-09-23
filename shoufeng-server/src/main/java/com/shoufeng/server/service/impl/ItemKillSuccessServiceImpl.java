package com.shoufeng.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoufeng.model.entity.ItemKillSuccessEntity;
import com.shoufeng.model.mapper.ItemKillSuccessMapper;
import com.shoufeng.server.service.IItemKillSuccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀成功订单表 服务实现类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@Service
public class ItemKillSuccessServiceImpl extends ServiceImpl<ItemKillSuccessMapper, ItemKillSuccessEntity> implements IItemKillSuccessService {

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Override
    public Integer countUserItemNum(Long userId, Long itemId) {
        return itemKillSuccessMapper.countUserItemNum(userId,itemId);
    }
}
