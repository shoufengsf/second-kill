package com.shoufeng.server.service;

/**
 * RabbitMQ发送邮件服务
 *
 * @author shoufeng
 */
public interface RabbitSenderService {
    void sendKillSuccessEmailMsg(String orderNo);

    void sendKillSuccessOrderExpireMsg(String orderCode);
}
