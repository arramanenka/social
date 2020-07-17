package com.romanenko.io;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseSupplier {
    Mono<ServerResponse> ok();

    Mono<ServerResponse> ok(Object object);

    Mono<ServerResponse> ok(Mono<?> object);

    Mono<ServerResponse> ok(Flux<?> flux);

    Mono<ServerResponse> badRequest(Throwable e);

    Mono<ServerResponse> badRequest(String message);

    Mono<ServerResponse> noContent();

    Mono<ServerResponse> error(Throwable e);
}
