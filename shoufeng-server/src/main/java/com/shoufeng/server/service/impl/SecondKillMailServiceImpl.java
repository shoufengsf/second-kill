package com.shoufeng.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.shoufeng.message.service.MailService;
import com.shoufeng.model.dto.ItemKillSuccessInfoDto;
import com.shoufeng.server.common.utils.FreeMarkerUtil;
import com.shoufeng.server.service.SecondKillMailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shoufeng
 */
@Service
public class SecondKillMailServiceImpl implements SecondKillMailService {

    @Autowired
    private FreeMarkerUtil freeMarkerUtil;

    @Autowired
    private MailService mailService;

    @Async
    @Override
    public void sendSecondKillSuccessMail(String toMailAddress, String username, String itemName) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("itemName", itemName);
        String htmlString = freeMarkerUtil.getHtmlString("/second_kill_success_mail.ftl", map);
        mailService.sendMail(toMailAddress, "秒杀成功", htmlString, null, null);
    }

    @RabbitListener(queues = {"mq.kill.item.success.email.queue"}, containerFactory = "singleListenerContainer")
    @Override
    public void sendSecondKillSuccessMail(ItemKillSuccessInfoDto itemKillSuccessInfoDto) {
        System.out.println("=============成功消费消息=============");
        sendSecondKillSuccessMail(itemKillSuccessInfoDto.getEmail(), itemKillSuccessInfoDto.getUserName(), itemKillSuccessInfoDto.getItemName());
        System.out.println(JSON.toJSONString(itemKillSuccessInfoDto));
    }
}
