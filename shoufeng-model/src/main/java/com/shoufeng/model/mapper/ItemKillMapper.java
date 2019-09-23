package com.shoufeng.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoufeng.model.dto.ItemKillInfoDto;
import com.shoufeng.model.entity.ItemKillEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 待秒杀商品表 Mapper 接口
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
public interface ItemKillMapper extends BaseMapper<ItemKillEntity> {
    @Select("SELECT itemKill.*, item.`name`, item.`code`, item.stock, item.purchase_time, " +
            "(CASE " +
            "WHEN (NOW() BETWEEN itemKill.start_time AND itemKill.end_time AND itemKill.total > 0) " +
            "THEN	1 " +
            "ELSE	0 " +
            "END) AS canKill " +
            "FROM item_kill AS itemKill LEFT JOIN item AS item ON itemKill.item_id = item.id " +
            "WHERE item.is_active = 1 AND itemKill.is_active = 1 AND item.stock > 0 ")
    List<ItemKillInfoDto> getActiveItemKillList();

    @Select("SELECT itemKill.*, item.`name`, item.`code`, item.stock, item.purchase_time, " +
            "(CASE " +
            "WHEN (NOW() BETWEEN itemKill.start_time AND itemKill.end_time AND itemKill.total > 0) " +
            "THEN	1 " +
            "ELSE	0 " +
            "END) AS canKill " +
            "FROM item_kill AS itemKill LEFT JOIN item AS item ON itemKill.item_id = item.id " +
            "WHERE item.is_active = 1 AND itemKill.is_active = 1 AND item.stock > 0 AND item.id=#{id} ")
    ItemKillInfoDto getItemKillById(@Param("id") Long id);

    @Update("UPDATE item_kill SET item_kill.total = (item_kill.total - 1) WHERE item_kill.item_id = #{itemId} AND item_kill.total > 0")
    Integer reduceItemKill(Long itemId);
}
