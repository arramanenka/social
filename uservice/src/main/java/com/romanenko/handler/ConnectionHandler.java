package com.romanenko.handler;

import com.romanenko.dao.ConnectionDao;
import com.romanenko.io.PageQuery;
import com.romanenko.io.ResponseSupplier;
import com.romanenko.model.User;
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

@Component
@RequiredArgsConstructor
public class ConnectionHandler implements Routable {

    private final ResponseSupplier responseSupplier;
    private final IdentityProvider identityProvider;
    private final ConnectionDao connectionDao;

    @Override
    public void declareRoute(ApiBuilder builder) {
        builder.get("/{id}/followers", this::getAllFollowers)
                .get("/{id}/following", this::getALlFollowing)
                .post("/follower/{id}", this::addFollower)
                .delete("/follower/{id}", this::removeFollower)

                .get("/blacklist", this::getAllBlacklisted)
                .post("/blacklist/{id}", this::addToBlacklist)
                .delete("/blacklist/{id}", this::removeFromBlacklist);
    }

    @NonNull
    private Mono<ServerResponse> getAllFollowers(ServerRequest request) {
        String id = request.pathVariable("id");
        Flux<User> users = identityProvider.getIdentity(request)
                .flatMapMany(identity -> connectionDao.getFollowersOfUser(identity, id, new PageQuery(request)));
        return responseSupplier.questionable_ok(users, User.class);
    }

    @NonNull
    private Mono<ServerResponse> getALlFollowing(ServerRequest request) {
        String id = request.pathVariable("id");
        Flux<User> users = identityProvider.getIdentity(request)
                .flatMapMany(identity -> connectionDao.getFollowedByUser(identity, id, new PageQuery(request)));
        return responseSupplier.questionable_ok(users, User.class);
    }

    @NonNull
    private Mono<ServerResponse> addFollower(ServerRequest request) {
        String followingId = request.pathVariable("id");
        Mono<Void> result = identityProvider.getIdentity(request)
                .flatMap(id -> connectionDao.addFollower(id, followingId));
        return responseSupplier.questionable_ok(result, Void.class);
    }

    @NonNull
    private Mono<ServerResponse> removeFollower(ServerRequest request) {
        String followingId = request.pathVariable("id");
        Mono<Void> result = identityProvider.getIdentity(request)
                .flatMap(id -> connectionDao.removeFollower(id, followingId));
        return responseSupplier.questionable_ok(result, Void.class);
    }

    @NonNull
    private Mono<ServerResponse> getAllBlacklisted(ServerRequest request) {
        Flux<User> users = identityProvider.getIdentity(request)
                .flatMapMany(identity -> connectionDao.getBlacklist(identity, new PageQuery(request)));
        return responseSupplier.questionable_ok(users, User.class);
    }

    @NonNull
    private Mono<ServerResponse> addToBlacklist(ServerRequest request) {
        String blacklistedUser = request.pathVariable("id");
        Mono<Void> result = identityProvider.getIdentity(request)
                .flatMap(id -> connectionDao.blacklist(id, blacklistedUser));
        return responseSupplier.questionable_ok(result, Void.class);
    }

    @NonNull
    private Mono<ServerResponse> removeFromBlacklist(ServerRequest request) {
        String blacklistedUser = request.pathVariable("id");
        Mono<Void> result = identityProvider.getIdentity(request)
                .flatMap(id -> connectionDao.removeFromBlacklist(id, blacklistedUser));
        return responseSupplier.questionable_ok(result, Void.class);
    }
}
