package com.romanenko.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
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
        http
                .csrf().disable();
        log.info("Disabled csrf for dev mode");
        return http.build();
    }

    @Component
    public static class WebfluxConfig implements WebFluxConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedMethods("PUT", "GET", "POST")
                    .maxAge(3600);
        }
    }
}
