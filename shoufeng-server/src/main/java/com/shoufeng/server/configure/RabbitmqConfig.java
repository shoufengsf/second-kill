package com.shoufeng.server.configure;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 通用化 Rabbitmq 配置
 *
 * @author shoufeng
 */
@Configuration
public class RabbitmqConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitmqConfig.class);

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一消费者
     *
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        return factory;
    }

    /**
     * 多个消费者
     *
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //确认消费模式-NONE
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(15);
        factory.setPrefetchCount(10);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                LOGGER.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                LOGGER.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
            }
        });
        return rabbitTemplate;
    }

    //构建异步发送邮箱通知的消息模型
    @Bean
    public Queue successEmailQueue() {
        return new Queue("mq.kill.item.success.email.queue", true);
    }

    @Bean
    public TopicExchange successEmailExchange() {
        return new TopicExchange("mq.kill.item.success.email.exchange", true, false);
    }

    @Bean
    public Binding successEmailBinding() {
        return BindingBuilder.bind(successEmailQueue()).to(successEmailExchange()).with("mq.kill.item.success.email.routing.key");
    }

    //死信队列
    @Bean
    public Queue successKillDeadQueue() {
        Map<String, Object> argsMap = Maps.newHashMap();
        argsMap.put("x-dead-letter-exchange", "mq.kill.item.success.kill.dead.exchange");
        argsMap.put("x-dead-letter-routing-key", "mq.kill.item.success.kill.dead.routing.key");
        return new Queue("mq.kill.item.success.kill.dead.queue", true, false, false, argsMap);
    }

    //基本交换机
    @Bean
    public TopicExchange successKillDeadbaseExchange() {
        return new TopicExchange("mq.kill.item.success.base.exchange", true, false);
    }

    //绑定：基本交换机+基本路由+死信队列
    @Bean
    public Binding successKillDeadBaseBinding(){
        return BindingBuilder.bind(successKillDeadQueue()).to(successKillDeadbaseExchange()).with("mq.kill.item.success.base.routing.key");
    }

    //死信交换机
    @Bean
    public TopicExchange successKillDeadExchange(){
        return new TopicExchange("mq.kill.item.success.kill.dead.exchange",true,false);
    }

    //真正队列
    @Bean
    public Queue successKillDeadRealQueue(){
        return new Queue("mq.kill.item.success.kill.dead.real.queue",true);
    }

    //绑定：死信交换机+死信路由+真正队列
    @Bean
    public Binding successKillDeadRealBinding(){
        return BindingBuilder.bind(successKillDeadRealQueue()).to(successKillDeadExchange()).with("mq.kill.item.success.kill.dead.routing.key");
    }
}






























































































