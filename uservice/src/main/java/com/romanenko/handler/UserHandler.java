package com.romanenko.handler;

import com.romanenko.dao.UserDao;
import com.romanenko.io.ResponseSupplier;
import com.romanenko.routing.ApiBuilder;
import com.romanenko.routing.Routable;
import com.romanenko.security.IdentityProvider;
import com.romanenko.model.User;
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
                .onErrorResume(NullPointerException.class, e -> responseSupplier.badRequest("Incomplete request body.", e))
                .onErrorResume(responseSupplier::error);
    }

    private Mono<ServerResponse> deleteUser(ServerRequest request) {
        return identityProvider.getIdentity(request)
                .flatMap(userDao::deleteById)
                .flatMap(e -> responseSupplier.ok())
                .onErrorResume(responseSupplier::error);
    }

    private Mono<ServerResponse> getUser(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<User> user = identityProvider.getIdentity(request)
                .flatMap(identity -> userDao.getUserById(identity, id));
        return responseSupplier.questionable_ok(user, User.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        String nickStart = request.pathVariable("nickStart");
        Flux<User> users = identityProvider.getIdentity(request)
                .flatMapMany(identity -> userDao.getAllByNickBeginning(identity, nickStart));
        return responseSupplier.questionable_ok(users, User.class);
    }
}
