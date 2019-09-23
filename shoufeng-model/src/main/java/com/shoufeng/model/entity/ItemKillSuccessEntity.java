package com.shoufeng.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 秒杀成功订单表
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("item_kill_success")
public class ItemKillSuccessEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 秒杀成功生成的订单编号
     */
    //默认使用雪花算法ID
    @TableId
    private Long code;

    /**
     * 商品id
     */
    private Long itemId;

    /**
     * 秒杀id
     */
    private Long killId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 秒杀结果: -1无效  0成功(未付款)  1已付款  2已取消
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
