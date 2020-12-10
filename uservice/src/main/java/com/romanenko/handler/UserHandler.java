package com.romanenko.handler;

import com.romanenko.dao.UserDao;
import com.romanenko.io.PageQuery;
import com.romanenko.io.ResponseSupplier;
import com.romanenko.model.User;
import com.romanenko.model.UserRecommendation;
import com.romanenko.routing.ApiBuilder;
import com.romanenko.routing.Routable;
import com.romanenko.security.IdentityProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.romanenko.io.RequestExtensionsKt.safeBodyToMono;

@Component
@RequiredArgsConstructor
public class UserHandler implements Routable {

    private final ResponseSupplier responseSupplier;
    private final IdentityProvider identityProvider;
    private final UserDao userDao;

    @Override
    public void declareRoute(ApiBuilder builder) {
        builder.get("/users/nickname/{nickStart}", this::getAll)
                .get("/users/recommendations", this::getRecommendations)
                .post("/user", this::saveUser)
                .get("/user/{id}", this::getUser)
                .delete("/user", this::deleteUser);
    }

    @NonNull
    private Mono<ServerResponse> getRecommendations(ServerRequest request) {
        Flux<UserRecommendation> users = identityProvider.getIdentity(request)
                .flatMapMany(identity -> userDao.getRecommendations(identity, new PageQuery(request)));
        return responseSupplier.ok(users, UserRecommendation.class);
    }

    @NonNull
    private Mono<ServerResponse> saveUser(ServerRequest request) {
        return identityProvider.getIdentity(request)
                .flatMap(e -> safeBodyToMono(request, User.class).doOnSuccess(u -> u.setId(e.getId())))
                .flatMap(userDao::saveUser)
                .flatMap(responseSupplier::ok)
                .onErrorResume(responseSupplier::error);
    }

    @NonNull
    private Mono<ServerResponse> deleteUser(ServerRequest request) {
        return identityProvider.getIdentity(request)
                .flatMap(userDao::deleteById)
                .flatMap(e -> responseSupplier.ok())
                .onErrorResume(responseSupplier::error);
    }

    @NonNull
    private Mono<ServerResponse> getUser(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<User> user = identityProvider.getIdentity(request)
                .flatMap(identity -> userDao.getUserById(identity, id));
        return responseSupplier.ok(user, User.class);
    }

    @NonNull
    public Mono<ServerResponse> getAll(ServerRequest request) {
        String nickStart = request.pathVariable("nickStart");
        Flux<User> users = identityProvider.getIdentity(request)
                .flatMapMany(identity -> userDao.getAllByNickBeginning(identity, nickStart, new PageQuery(request)));
        return responseSupplier.ok(users, User.class);
    }
}
