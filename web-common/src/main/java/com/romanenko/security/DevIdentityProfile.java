package com.romanenko.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Profile("dev")
@Component
public class DevIdentityProfile {

    @Bean
    public IdentityProvider identityProvider() {
        return request -> Mono.just(() -> request.queryParam("id").orElse("id"));
    }

}
