package com.romanenko.routing;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

@Component
@ConditionalOnProperty(name = "dynamic.routing", havingValue = "true", matchIfMissing = true)
public class Router {
    @Bean
    public RouterFunction<ServerResponse> route(List<Routable> routables, ApiBuilder apiBuilder) {
        routables.forEach(e -> e.declareRoute(apiBuilder));
        return apiBuilder.build();
    }
}
