package com.romanenko.io;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ResponseSupplier {
    Mono<ServerResponse> ok();

    Mono<ServerResponse> ok(Object o);

    Mono<ServerResponse> badRequest(Throwable e);
}
