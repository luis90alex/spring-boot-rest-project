package com.restlearningjourney.store.common;

import org.springframework.boot.ApplicationRunner;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class HomeAppSecurityRules implements AppSecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(
                        "/",
                        "/index",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**"
                ).permitAll();
    }
}
