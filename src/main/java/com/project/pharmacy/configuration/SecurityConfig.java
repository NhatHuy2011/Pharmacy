package com.project.pharmacy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_POST_ENDPOINTS = {
        "/user",
        "/auth/log-in",
        "/auth/introspect",
        "/auth/logout",
        "/auth/outbound/authentication/**",
        "/auth/refresh",
        "/cart/guest",
        "/order/guest/**",
        "/vnpay/create-payment/**",
        "/momo/create-payment",
        "/zalopay/create-payment",
        "/callback",
        "/invoice",
        "/notifications/send",
        "/delivery/**"
    };

    private final String[] PUBLIC_GET_ENDPOINTS = {
            "/product/**",
            "/category/**",
            "/unit",
            "/price",
            "/company",
            "/cart/guest",
            "/order/**",
            "/feedback/**",
            "/home/**",
            "/delivery/**",
            "/address/detail"
    };

    private final String[] PUBLIC_PUT_ENDPOINTS = {
        "/user/verify-email-signup",
        "/user/forgot-password",
        "/user/reset-password",
        "/user/refresh-otp",
        "/cart/guest"
    };

    private final String[] PUBLIC_DELETE_ENDPOINTS = {"/cart/guest"};

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers("/ws-notifications/**")
                .permitAll()
                .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.PUT, PUBLIC_PUT_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.DELETE, PUBLIC_DELETE_ENDPOINTS)
                .permitAll()
                //.requestMatchers(HttpMethod.GET, "/product")//.hasRole(Role.ADMIN.name())
                .anyRequest()
                .authenticated());

        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())); // Tao ra provider manager

        httpSecurity.csrf(AbstractHttpConfigurer::disable); // Mac dinh bat csrf //(httpSecurityCsrfConfigurer ->
        // httpSecurityCsrfConfigurer.disable())
        httpSecurity.cors(cors -> cors.configurationSource(corsFilter()));
        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // Da set trong build scope

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Chỉ định rõ các origin được phép (ví dụ: localhost:3000)
        config.setAllowedOriginPatterns(List.of("*"));

        // Chỉ định các header được phép
        config.addAllowedHeader("*");

        // Chỉ định các phương thức được phép
        config.addAllowedMethod("*");

        // Cho phép gửi cookie và các thông tin xác thực
        config.setAllowCredentials(true);

        // Tạo nguồn cấu hình
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    } // Cau hinh Cors để cho phép may chu Spring Boot goi cac yeu cau tu nguon. Vi du: http://localhost:3000

    @Bean // Khi danh dau Bean thi Bien nay se duoc dua vao Application Context de su dung o nhung noi khac
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
