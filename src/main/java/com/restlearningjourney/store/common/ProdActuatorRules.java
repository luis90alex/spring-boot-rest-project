package com.restlearningjourney.store.common;

import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdActuatorRules implements ActuatorSecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/prometheus").hasRole("ACTUATOR")
                .requestMatchers("/actuator/info").hasRole("ACTUATOR");
    }
}
