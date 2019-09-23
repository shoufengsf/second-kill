package com.shoufeng.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoufeng.model.entity.ItemEntity;
import com.shoufeng.model.mapper.ItemMapper;
import com.shoufeng.server.service.IItemService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, ItemEntity> implements IItemService {

}
