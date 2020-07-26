package com.romanenko.handler;

import com.romanenko.routing.ApiBuilder;
import com.romanenko.routing.Routable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MessageHandler implements Routable {
    @Override
    public void declareRoute(ApiBuilder builder) {
        builder
                .post("/message/{userId}", this::postMessage)
                .delete("/message/{userId}/{messageId}", this::deleteMessage)
                .get("/messages/{userId}", this::getMessages);
    }

    private Mono<ServerResponse> postMessage(ServerRequest request) {
        return null;
    }

    private Mono<ServerResponse> deleteMessage(ServerRequest request) {
        return null;
    }

    private Mono<ServerResponse> getMessages(ServerRequest request) {
        return null;
    }
}
