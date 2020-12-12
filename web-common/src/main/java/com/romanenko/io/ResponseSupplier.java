package com.romanenko.io;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseSupplier {
    Mono<ServerResponse> ok();

    Mono<ServerResponse> ok(FileSystemResource fileSystemResource);

    Mono<ServerResponse> ok(Object object);

    <T> Mono<ServerResponse> ok(Mono<T> object, Class<T> clazz);

    <T> Mono<ServerResponse> ok(Flux<T> flux, Class<T> clazz);

    Mono<ServerResponse> badRequest(String message, Exception e);

    Mono<ServerResponse> badRequest(String message);

    Mono<ServerResponse> noContent();

    Mono<ServerResponse> error(Throwable e);

    @NotNull
    Mono<ServerResponse> notFound(@NotNull String message);
}
