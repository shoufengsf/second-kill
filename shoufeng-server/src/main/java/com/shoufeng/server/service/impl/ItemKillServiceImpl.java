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
import com.shoufeng.server.common.utils.RedisUtil;
import com.shoufeng.server.service.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private final static String ZOOKEEPER_PATH_PREFIX = "/second_kill/mylock/";
    @Autowired
    private ItemKillMapper itemKillMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;
    @Autowired
    private ValueOperations<String, String> valueOperations;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private CuratorFramework curatorFramework;
    @Autowired
    private SecondKillMailService secondKillMailService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private RabbitSenderService rabbitSenderService;
    @Autowired
    private IItemKillSuccessService iItemKillSuccessService;

    @Override
    public List<ItemKillInfoDto> findActiveItemKillList() {
        return itemKillMapper.getActiveItemKillList();
    }

    @Override
    public ItemKillInfoDto findItemKillById(Long id) {
        return itemKillMapper.getItemKillById(id);
    }

    /**
     * redis实现分布式锁实现一：利用valueOperations
     *
     * @param userId
     * @param itemId
     * @return
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Boolean killItemRedisLock(Long userId, Long itemId) {
        Boolean isSuccess = false;
        String key = itemId + userId + "-RedisLock";
        Boolean flag = valueOperations.setIfAbsent(key, "", 300, TimeUnit.SECONDS);
        //该判断代码块不能进入try代码块，因为会导致获取锁失败后，将锁删除
        if (flag == null || !flag) {
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, "获取锁失败");
            throw new ServiceException("秒杀失败");
        }
        try {
            isSuccess = killItemBase(userId, itemId);
        } finally {
            redisUtil.delete(key);
        }
        return isSuccess;
    }

    /**
     * redis实现分布式锁实现二：利用redisson
     *
     * @param userId
     * @param itemId
     * @return
     */
    @Override
    public Boolean killItemRedissonLock(Long userId, Long itemId) {
        String key = itemId + userId + "-RedisLock";
        RLock fairLock = redissonClient.getFairLock(key);
        try {
            fairLock.tryLock(10, TimeUnit.SECONDS);
            return killItemBase(userId, itemId);
        } catch (InterruptedException e) {
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, e.getLocalizedMessage());
            LOGGER.error("秒杀失败: ", e);
        } finally {
            fairLock.unlock();
        }
        return false;
    }

    /**
     * zookeeper实现分布式锁
     *
     * @param userId
     * @param itemId
     * @return
     */
    @Override
    public Boolean killItemZKLock(Long userId, Long itemId) {
        //zookeeper实现分布式锁
        String lockKey = ZOOKEEPER_PATH_PREFIX + userId + itemId + "-lock";
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, lockKey);
        try {
            mutex.acquire(10, TimeUnit.SECONDS);
            return killItemBase(userId, itemId);
        } catch (Exception e) {
            LOGGER.info("秒杀失败: userId({}), itemId({}), message({})", userId, itemId, e.getLocalizedMessage());
            LOGGER.error("秒杀失败: ", e);
        } finally {
            try {
                if (mutex.isAcquiredInThisProcess()) {
                    mutex.release();
                }
            } catch (Exception e) {
                LOGGER.error("释放" + lockKey + "锁失败: ", e);
            }
        }
        return null;
    }

    @Override
    public Boolean killItemBase(Long userId, Long itemId) {

        ItemKillInfoDto itemKillInfo = findItemKillById(itemId);
        if (itemKillInfo == null) {
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

        //秒杀成功后发送邮件
//        UserEntity userEntity = iUserService.getById(userId);
//        secondKillMailService.sendSecondKillSuccessMail(userEntity.getEmail(), userEntity.getUserName(), itemKillInfo.getName());
        rabbitSenderService.sendKillSuccessEmailMsg(itemKillSuccess.getCode().toString());
        return true;
    }
}
