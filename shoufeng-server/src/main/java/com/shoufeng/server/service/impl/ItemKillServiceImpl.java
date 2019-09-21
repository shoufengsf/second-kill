package com.shoufeng.server.service.impl;

import com.shoufeng.model.entity.ItemKill;
import com.shoufeng.model.mapper.ItemKillMapper;
import com.shoufeng.server.service.IItemKillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
