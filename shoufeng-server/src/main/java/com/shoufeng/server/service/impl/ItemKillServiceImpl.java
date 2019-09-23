package com.shoufeng.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoufeng.model.dto.ItemKillInfoDto;
import com.shoufeng.model.entity.ItemKillEntity;
import com.shoufeng.model.entity.ItemKillSuccessEntity;
import com.shoufeng.model.mapper.ItemKillMapper;
import com.shoufeng.model.mapper.ItemKillSuccessMapper;
import com.shoufeng.model.mapper.ItemMapper;
import com.shoufeng.server.common.constant.KillStatusEnum;
import com.shoufeng.server.common.exception.ServiceException;
import com.shoufeng.server.service.IItemKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 待秒杀商品表 服务实现类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@Service
public class ItemKillServiceImpl extends ServiceImpl<ItemKillMapper, ItemKillEntity> implements IItemKillService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ItemKillServiceImpl.class);

    @Autowired
    private ItemKillMapper itemKillMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    private ValueOperations<String, String> valueOperations;

    @Override
    public List<ItemKillInfoDto> findActiveItemKillList() {
        return itemKillMapper.getActiveItemKillList();
    }

    @Override
    public ItemKillInfoDto findItemKillById(Long id) {
        return itemKillMapper.getItemKillById(id);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Boolean killItem(Long userId, Long itemId) {

        //redis实现分布式锁
        String key = itemId + userId + "-RedisLock";;
        Boolean flag = valueOperations.setIfAbsent(key, "", 30, TimeUnit.SECONDS);
        if (flag == null || !flag) {
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, "获取锁失败");
            throw new ServiceException("秒杀失败");
        }

        ItemKillInfoDto itemKillInfo = findItemKillById(itemId);
        if (itemKillInfo == null){
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, "不存在该秒杀商品");
            throw new ServiceException("不存在该秒杀商品");
        }

        Integer canKill = itemKillInfo.getCanKill();
        //标志位为1时可以秒杀
        if (canKill != 1) {
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, "该商品不可秒杀");
            throw new ServiceException("该商品不可秒杀");
        }

        //一个用户对于一个商品只可以秒杀一次
        Integer itemNum = itemKillSuccessMapper.countUserItemNum(userId, itemId);
        if (itemNum != 0) {
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, "秒杀失败,该用户已经抢购到该商品，不允许重复抢购");
            throw new ServiceException("秒杀失败,该用户已经抢购到该商品，不允许重复抢购");
        }

        Integer itemKillFlag = itemKillMapper.reduceItemKill(itemId);
        if (itemKillFlag != 1) {
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, "更新秒杀表（item_kill）库存失败");
            throw new ServiceException("秒杀失败");
        }

        Integer itemFlag = itemMapper.reduceItem(itemId);
        if (itemFlag != 1) {
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, "更新商品表（item）库存失败");
            throw new ServiceException("秒杀失败");
        }

        ItemKillSuccessEntity itemKillSuccess = new ItemKillSuccessEntity();
        itemKillSuccess.setKillId(itemKillInfo.getId());
        itemKillSuccess.setItemId(itemKillInfo.getItemId());
        itemKillSuccess.setStatus(KillStatusEnum.SUCCESS.getCode());
        itemKillSuccess.setUserId(userId);
        itemKillSuccessMapper.insert(itemKillSuccess);
        return true;
    }
}
