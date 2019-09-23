package com.shoufeng.model.dto;

import com.shoufeng.model.entity.ItemKillEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author shoufeng
 */
@Data
public class ItemKillInfoDto extends ItemKillEntity implements Serializable {

    /**
     * 商品名
     */
    private String name;

    /**
     * 商品编号
     */
    private String code;

    /**
     * 库存
     */
    private Long stock;

    /**
     * 采购时间
     */
    private LocalDate purchaseTime;

    /**
     * 能否被秒杀标志位 1: 可以
     */
    private Integer canKill;

}
