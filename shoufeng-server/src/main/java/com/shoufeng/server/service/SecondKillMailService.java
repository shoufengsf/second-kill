package com.shoufeng.server.service;

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
}
