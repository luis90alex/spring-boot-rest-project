package com.restlearningjourney.store.common;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class SwaggerSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/swagger-iu/**").permitAll()
                .requestMatchers("/swagger-iu.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll();
    }
}
