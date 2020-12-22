package com.netease.cloud.nsf.demo.stock.viewer.web.configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ConditionalOnProperty(name="dubbo", havingValue="true")
@EnableDubbo(scanBasePackages = "com.netease.cloud.nsf.demo.stock.viewer")
@ComponentScan(value = {"com.netease.cloud.nsf.demo.stock.viewer"})
public class DubboConfiguration {

    @Value("${nsf.zk}")
    public String zk;

    @Value("${nsf.app:nsf-demo-stock-viewer}")
    public String app;

    @Value("${nsf.port}")
    public int port;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig application = new ApplicationConfig();
        application.setName(app);
        return application;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(zk);
        return registryConfig;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(port);
        return protocolConfig;
    }


}
