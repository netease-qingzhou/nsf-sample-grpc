package com.netease.cloud.nsf.demo.grpc;

import com.netease.cloud.nsf.demo.grpc.web.interceptor.ColorInterceptor;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StockGrpcConsumerApplication extends SpringBootServletInitializer {

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurerAdapter() {
        return registry -> registry.addServerInterceptors(new ColorInterceptor());
    }

    public static void main(String[] args) {
        SpringApplication.run(StockGrpcConsumerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StockGrpcConsumerApplication.class);
    }
}
