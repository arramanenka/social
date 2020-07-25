package com.romanenko.routing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class ApiBuilder {
    private final RouterFunctions.Builder builder = RouterFunctions.route();

    public ApiBuilder get(String path, HandlerFunction<ServerResponse> handlerFunction) {
        log.debug("{} was mapped as get mapping", path);
        builder.GET(path, handlerFunction);
        return this;
    }

    public ApiBuilder post(String path, HandlerFunction<ServerResponse> handlerFunction) {
        log.debug("{} was mapped as post mapping", path);
        builder.POST(path, RequestPredicates.accept(MediaType.APPLICATION_JSON), handlerFunction);
        return this;
    }

    public ApiBuilder delete(String path, HandlerFunction<ServerResponse> handlerFunction) {
        log.debug("{} was mapped as delete mapping", path);
        builder.DELETE(path, handlerFunction);
        return this;
    }

    public RouterFunction<ServerResponse> build() {
        return builder.build();
    }
}