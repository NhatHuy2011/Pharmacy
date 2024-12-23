package com.project.pharmacy.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary configKey() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dvyvp4n4p");
        config.put("api_key", "271817863714469");
        config.put("api_secret", "XNsmP1ffY3JH3mmFZlMdQiIBS40");
        return new Cloudinary(config);
    }
}
