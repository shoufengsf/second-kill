package com.shoufeng.model.dto;

import lombok.Data;

/**
 * 秒杀成功相关信息
 *
 * @author shoufeng
 */
@Data
public class ItemKillSuccessInfoDto {
    private String userName;
    private String email;
    private String itemName;
}
