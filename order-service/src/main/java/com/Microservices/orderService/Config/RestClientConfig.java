package com.Microservices.orderService.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {


    @Bean(name = "inventoryWebClient")
    public WebClient inventoryWebClient(
            @Value("${app.services.inventory-base-url}") String baseUrl)
    {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(2));


        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
/*
* @Bean → Put tool in toolbox(OBJECT IN IOC CONTAINER )
@Value → Read config
builder() → Start assembling
baseUrl() → Set default address
build() → Final product
@Autowired/constructor injection → Use it anywhere in class
* */






    @Bean(name = "paymentWebClient")
    public WebClient paymentWebClient(
            @Value("${app.services.payment-base-url}") String baseUrl)
    {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(2));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

