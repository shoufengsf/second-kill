package com.shoufeng.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shoufeng.model.entity.Item;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
public interface ItemMapper extends BaseMapper<Item> {
    @Update("UPDATE item SET item.stock = (item.stock - 1) WHERE item.id = #{itemId} AND item.stock > 0")
    Integer reduceItem(@Param("itemId") Long itemId);
}
