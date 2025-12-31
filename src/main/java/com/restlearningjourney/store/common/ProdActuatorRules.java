package com.restlearningjourney.store.common;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
@Order(0)
@Profile("prod")
public class ActuatorProdSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/actuator/prometheus").hasRole("ACTUATOR")
                .requestMatchers("/actuator/info").hasRole("ACTUATOR")
                .anyRequest().denyAll();
    }
}
