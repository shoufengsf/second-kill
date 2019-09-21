package com.shoufeng.server.service.impl;

import com.shoufeng.model.entity.Item;
import com.shoufeng.model.mapper.ItemMapper;
import com.shoufeng.server.service.IItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

}
