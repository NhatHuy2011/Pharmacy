package com.project.pharmacy.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignHeaderDeliveryInterceptor implements RequestInterceptor {
    @Value("${ghn.token}")
    private String token;

    @Value("${ghn.shop_id}")
    private String shopId;

    @Override
    public void apply(RequestTemplate template) {
        template.header("Content-Type", "application/json");
        template.header("Token", token);
        template.header("ShopId", shopId);
    }
}
