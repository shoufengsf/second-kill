package com.shoufeng.server.service;

import com.shoufeng.model.dto.ItemKillSuccessInfoDto;

/**
 * 秒杀邮件服务
 *
 * @author shoufeng
 */
public interface SecondKillMailService {
    /**
     * 发送秒杀成功邮件
     *
     * @param toMailAddress 接受人邮箱地址
     * @param username      用户名
     * @param itemName      商品名
     */
    void sendSecondKillSuccessMail(String toMailAddress, String username, String itemName);

    /**
     * 秒杀成功后消费rabbitmq
     * @param itemKillSuccessInfoDto 秒杀成功相关信息
     */
    void sendSecondKillSuccessMail(ItemKillSuccessInfoDto itemKillSuccessInfoDto);

    void realQueueCustomer(ItemKillSuccessInfoDto itemKillSuccessInfoDto);

    void deadQueueCustomer(ItemKillSuccessInfoDto itemKillSuccessInfoDto);
}
