package com.romanenko.io;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
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
    public Mono<ServerResponse> ok(FileSystemResource fileSystemResource) {
        return ServerResponse.ok().bodyValue(fileSystemResource);
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
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(flux, clazz);
    }

    @Override
    public Mono<ServerResponse> badRequest(String message, Exception e) {
        log.error(e);
        return badRequest(message);
    }

    @Override
    public Mono<ServerResponse> badRequest(String message) {
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createResponseMessage(message));
    }

    @Override
    public Mono<ServerResponse> error(Throwable e) {
        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException exception = (HttpStatusCodeException) e;
            return ServerResponse.status(exception.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createResponseMessage(exception.getStatusText()));
        } else if (e instanceof ResponseStatusException) {
            ResponseStatusException exception = (ResponseStatusException) e;
            return ServerResponse.status(exception.getStatus())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createResponseMessage(exception.getReason()));
        }
        log.error(e);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @NotNull
    @Override
    public Mono<ServerResponse> notFound(@NotNull String message) {
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createResponseMessage(message));
    }

    private String createResponseMessage(String message) {
        return "{\"details\":\"" + message + "\"}";
    }
}
