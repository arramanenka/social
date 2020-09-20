package com.romanenko.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Log4j2
@Profile("dev")
@Component
public class DevIdentityProfile {

    @Bean
    public IdentityProvider identityProvider() {
        return request -> Mono.just(() -> request.queryParam("id").orElse("id"));
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        var configSource = new CorsConfiguration();
        configSource.addAllowedOrigin("*");
        for (HttpMethod value : HttpMethod.values()) {
            configSource.addAllowedMethod(value);
        }
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configSource);

        http
                .cors().configurationSource(source);
        log.info("Disabled cors and csrf for dev mode");
        return http.build();
    }

}
