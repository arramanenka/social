package com.romanenko.uservice.handler;

import com.romanenko.io.ResponseSupplier;
import com.romanenko.routing.ApiBuilder;
import com.romanenko.routing.Routable;
import com.romanenko.security.IdentityProvider;
import com.romanenko.uservice.UserDao;
import com.romanenko.uservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler implements Routable {

    private final ResponseSupplier responseSupplier;
    private final IdentityProvider identityProvider;
    private final UserDao userDao;

    @Override
    public void declareRoute(ApiBuilder builder) {
        builder.get("/users/nickname/{nickStart}", this::getAll)
                .post("/user", this::saveUser)
                .get("/user/{id}", this::getUser)
                .delete("/user", this::deleteUser);
    }

    private Mono<ServerResponse> saveUser(ServerRequest request) {
        return identityProvider.getIdentity(request)
                .flatMap(e -> request.bodyToMono(User.class).doOnSuccess(u -> u.setId(e.getId())))
                .flatMap(userDao::saveUser)
                .flatMap(responseSupplier::ok)
                .onErrorResume(responseSupplier::badRequest);
    }

    private Mono<ServerResponse> deleteUser(ServerRequest request) {
        return identityProvider.getIdentity(request)
                .flatMap(userDao::deleteById)
                .flatMap(e -> responseSupplier.ok())
                .onErrorResume(responseSupplier::badRequest);
    }

    private Mono<ServerResponse> getUser(ServerRequest request) {
        Mono<User> user = userDao.getUserById(request.pathVariable("id"));
        return responseSupplier.ok(user);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        Flux<User> users = userDao.getAllByNickBeginning(request.pathVariable("nickStart"));
        return responseSupplier.ok(users);
    }
}
