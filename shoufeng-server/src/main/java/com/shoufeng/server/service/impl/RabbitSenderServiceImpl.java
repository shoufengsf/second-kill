package com.shoufeng.server.service.impl;

import com.shoufeng.model.dto.ItemKillInfoDto;
import com.shoufeng.model.dto.ItemKillSuccessInfoDto;
import com.shoufeng.model.mapper.ItemKillSuccessMapper;
import com.shoufeng.server.service.RabbitSenderService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shoufeng
 */
@Service
public class RabbitSenderServiceImpl implements RabbitSenderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;


    @Override
    public void sendKillSuccessEmailMsg(String orderNo) {
        ItemKillSuccessInfoDto itemKillSuccessInfoDto = itemKillSuccessMapper.selectItemKillInfoByCode(orderNo);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange("mq.kill.item.success.email.exchange");
        rabbitTemplate.setRoutingKey("mq.kill.item.success.email.routing.key");
        rabbitTemplate.convertAndSend(itemKillSuccessInfoDto, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties messageProperties = message.getMessageProperties();
                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, ItemKillSuccessInfoDto.class);
                return message;
            }
        });
    }

    // TODO: 2019/9/26 要结合实际需求
    @Override
    public void sendKillSuccessOrderExpireMsg(String orderCode) {
        //基本交换机+基本路由-->>死信队列
//        rabbitTemplate.convertAndSend("mq.kill.item.success.base.exchange","mq.kill.item.success.base.routing.key","死信消息测试");
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange("mq.kill.item.success.base.exchange");
        rabbitTemplate.setRoutingKey("mq.kill.item.success.base.routing.key");
        ItemKillSuccessInfoDto itemKillSuccessInfoDto = itemKillSuccessMapper.selectItemKillInfoByCode(orderCode);
        itemKillSuccessInfoDto.setUserName("死信队列测试");
        rabbitTemplate.convertAndSend(itemKillSuccessInfoDto, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties mp=message.getMessageProperties();
                mp.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                mp.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,ItemKillSuccessInfoDto.class);

                //TODO：动态设置TTL(为了测试方便，暂且设置10s)
                mp.setExpiration("10000");
                return message;
            }
        });
    }
}
