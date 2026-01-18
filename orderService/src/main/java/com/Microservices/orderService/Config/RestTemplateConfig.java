package com.Microservices.orderService.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        // 2 seconds max to connect
        factory.setConnectTimeout(2000);

        // 2 seconds max to wait for response
        factory.setReadTimeout(2000);

        return new RestTemplate(factory);
    }
}
