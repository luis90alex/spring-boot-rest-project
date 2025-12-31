package com.restlearningjourney.store.payments;

import com.restlearningjourney.store.common.AppSecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class PaymentAppSecurityRule implements AppSecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(HttpMethod.GET, "/checkout-success", "/checkout-cancel").permitAll()
                .requestMatchers(HttpMethod.POST,"/checkout/webhook").permitAll();

    }
}
