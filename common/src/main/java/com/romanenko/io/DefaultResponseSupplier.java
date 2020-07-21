package com.romanenko.io;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class DefaultResponseSupplier implements ResponseSupplier {
    @Override
    public Mono<ServerResponse> noContent() {
        return ServerResponse.noContent().build();
    }

    @Override
    public Mono<ServerResponse> ok() {
        return ServerResponse.ok().build();
    }

    @Override
    public Mono<ServerResponse> ok(Object object) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(object);
    }

    @Override
    public <T> Mono<ServerResponse> ok(Mono<T> object, Class<T> clazz) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(object, clazz);
    }

    @Override
    public <T> Mono<ServerResponse> ok(Flux<T> flux, Class<T> clazz) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(flux, clazz);
    }

    @Override
    public Mono<ServerResponse> badRequest(String message) {
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message);
    }

    @Override
    public Mono<ServerResponse> error(Throwable e) {
        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException exception = (HttpStatusCodeException) e;
            String message = exception.getResponseBodyAsString();
            HttpStatus statusCode = exception.getStatusCode();
            return ServerResponse.status(statusCode).bodyValue(message);
        }
        log.error(e);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}