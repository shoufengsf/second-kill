package com.shoufeng.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoufeng.model.dto.ItemKillInfo;
import com.shoufeng.model.entity.ItemKill;
import com.shoufeng.model.entity.ItemKillSuccess;
import com.shoufeng.model.mapper.ItemKillMapper;
import com.shoufeng.model.mapper.ItemKillSuccessMapper;
import com.shoufeng.model.mapper.ItemMapper;
import com.shoufeng.server.common.constant.KillStatusEnum;
import com.shoufeng.server.common.exception.ServiceException;
import com.shoufeng.server.service.IItemKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 待秒杀商品表 服务实现类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@Service
public class ItemKillServiceImpl extends ServiceImpl<ItemKillMapper, ItemKill> implements IItemKillService {

    @Autowired
    private ItemKillMapper itemKillMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Override
    public List<ItemKillInfo> findActiveItemKillList() {
        return itemKillMapper.getActiveItemKillList();
    }

    @Override
    public ItemKillInfo findItemKillById(Long id) {
        return itemKillMapper.getItemKillById(id);
    }

    @Override
    public Boolean killItem(Long userId, Long itemId) {
        ItemKillInfo itemKillInfo = findItemKillById(itemId);
        if (itemKillInfo == null){
            throw new ServiceException("不存在该秒杀商品");
        }
        Integer canKill = itemKillInfo.getCanKill();
        //标志位为1时可以秒杀
        if (1 == canKill){
            Integer itemKillFlag = itemKillMapper.reduceItemKill(itemId);
            if (itemKillFlag != 1){
                throw new ServiceException("秒杀失败");
            }
            Integer itemFlag = itemMapper.reduceItem(itemId);
            if (itemFlag != 1){
                throw new ServiceException("秒杀失败");
            }
            ItemKillSuccess itemKillSuccess = new ItemKillSuccess();
            itemKillSuccess.setKillId(itemKillInfo.getId());
            itemKillSuccess.setItemId(itemKillInfo.getItemId());
            itemKillSuccess.setStatus(KillStatusEnum.SUCCESS.getCode());
            itemKillSuccess.setUserId(userId);
            itemKillSuccessMapper.insert(itemKillSuccess);
            return true;
        }
        return false;
    }
}
