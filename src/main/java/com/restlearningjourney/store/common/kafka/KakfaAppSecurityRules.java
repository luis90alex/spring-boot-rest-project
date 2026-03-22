package com.restlearningjourney.store.common.kafka;


import com.restlearningjourney.store.common.AppSecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class KakfaAppSecurityRules implements AppSecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(HttpMethod.POST,"/producer").permitAll()
                .requestMatchers(HttpMethod.POST, "/dlq").permitAll();
    }
}
