package com.romanenko.security;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface IdentityProvider {
    Mono<Identity> getIdentity(ServerRequest request);
}
