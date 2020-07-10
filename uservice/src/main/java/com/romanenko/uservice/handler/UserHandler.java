package com.romanenko.uservice.handler;

import com.romanenko.routing.ApiBuilder;
import com.romanenko.routing.Routable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler implements Routable {

    @Override
    public void declareRoute(ApiBuilder builder) {
        builder.get("/users", this::getAll)
                .post("/user", this::saveUser)
                .get("/user/{id}", this::getUser)
                .delete("/user", this::deleteUser);
    }

    private Mono<ServerResponse> deleteUser(ServerRequest request) {
        return Mono.empty();
    }

    private Mono<ServerResponse> getUser(ServerRequest request) {
        return Mono.empty();
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return Mono.empty();
    }

    private Mono<ServerResponse> saveUser(ServerRequest request) {
        return Mono.empty();
    }
}
