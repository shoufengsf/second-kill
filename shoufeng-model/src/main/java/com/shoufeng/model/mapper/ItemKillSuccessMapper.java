package com.shoufeng.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoufeng.model.dto.ItemKillInfoDto;
import com.shoufeng.model.entity.ItemKillSuccessEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 秒杀成功订单表 Mapper 接口
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
public interface ItemKillSuccessMapper extends BaseMapper<ItemKillSuccessEntity> {
    @Select("SELECT COUNT(item_kill_success.`code`) FROM item_kill_success WHERE item_kill_success.user_id = #{userId} AND item_kill_success.item_id = #{itemId}")
    public Integer countUserItemNum(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Select("SELECT item.*, item_kill.* FROM item_kill_success LEFT JOIN item_kill ON item_kill.id = item_kill_success.kill_id LEFT JOIN item ON item.id = item_kill.item_id WHERE item_kill_success.`code` = #{code}")
    public ItemKillInfoDto selectItemKillInfoByCode(String code);
}
