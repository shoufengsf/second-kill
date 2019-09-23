package com.shoufeng.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoufeng.model.dto.ItemKillInfo;
import com.shoufeng.model.entity.ItemKill;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 待秒杀商品表 Mapper 接口
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
public interface ItemKillMapper extends BaseMapper<ItemKill> {
    @Select("SELECT itemKill.*, item.`name`, item.`code`, item.stock, item.purchase_time " +
            "FROM item_kill AS itemKill LEFT JOIN item AS item ON itemKill.item_id = item.id " +
            "WHERE item.is_active = 1 AND itemKill.is_active = 1")
    public List<ItemKillInfo> getActiveItemKillList();
}
