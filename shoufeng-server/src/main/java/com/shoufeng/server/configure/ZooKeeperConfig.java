package com.shoufeng.server.configure;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zookeeper配置
 *
 * @author shoufeng
 */
@Configuration
public class ZooKeeperConfig {

    @Value("${zookeeper.host}")
    private String zkHost;

    @Value("${zookeeper.namespace}")
    private String zkNamespace;


    /**
     * 自定义注入ZooKeeper客户端操作实例
     *
     * @return
     */
    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zkHost)
                .namespace(zkNamespace)
                //重试策略
                .retryPolicy(new RetryNTimes(5, 1000))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }
}
