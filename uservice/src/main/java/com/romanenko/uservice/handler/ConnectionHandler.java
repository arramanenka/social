package com.romanenko.uservice.handler;

import com.romanenko.io.ResponseSupplier;
import com.romanenko.routing.ApiBuilder;
import com.romanenko.routing.Routable;
import com.romanenko.security.IdentityProvider;
import com.romanenko.uservice.dao.ConnectionDao;
import lombok.RequiredArgsConstructor;
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

    private Mono<ServerResponse> getAllFollowers(ServerRequest request) {
        String id = request.pathVariable("id");
        Flux<String> users = identityProvider.getIdentity(request)
                .flatMapMany(identity -> connectionDao.getFollowersOfUser(identity, id));
        return responseSupplier.ok(users, String.class);
    }

    private Mono<ServerResponse> getALlFollowing(ServerRequest request) {
        String id = request.pathVariable("id");
        Flux<String> users = identityProvider.getIdentity(request)
                .flatMapMany(identity -> connectionDao.getFollowedByUser(identity, id));
        return responseSupplier.ok(users, String.class);
    }

    private Mono<ServerResponse> addFollower(ServerRequest request) {
        String followingId = request.pathVariable("id");
        return identityProvider.getIdentity(request)
                .flatMap(id -> connectionDao.addFollower(id, followingId))
                .filter(e -> e)
                .flatMap(e -> responseSupplier.noContent())
                .switchIfEmpty(responseSupplier.badRequest("Could not add follower."))
                .onErrorResume(responseSupplier::error);
    }

    private Mono<ServerResponse> removeFollower(ServerRequest request) {
        String followingId = request.pathVariable("id");
        return identityProvider.getIdentity(request)
                .flatMap(id -> connectionDao.removeFollower(id, followingId))
                .filter(e -> e)
                .flatMap(e -> responseSupplier.noContent())
                .switchIfEmpty(responseSupplier.badRequest("Could not remove follower."))
                .onErrorResume(responseSupplier::error);
    }

    private Mono<ServerResponse> getAllBlacklisted(ServerRequest request) {
        Flux<String> users = identityProvider.getIdentity(request)
                .flatMapMany(connectionDao::getBlacklist);
        return responseSupplier.ok(users, String.class);
    }

    private Mono<ServerResponse> addToBlacklist(ServerRequest request) {
        String blacklistedUser = request.pathVariable("id");
        return identityProvider.getIdentity(request)
                .flatMap(id -> connectionDao.blacklist(id, blacklistedUser))
                .filter(e -> e)
                .flatMap(e -> responseSupplier.noContent())
                .switchIfEmpty(responseSupplier.badRequest("Could not put user into blacklist."))
                .onErrorResume(responseSupplier::error);
    }

    private Mono<ServerResponse> removeFromBlacklist(ServerRequest request) {
        String blacklistedUser = request.pathVariable("id");
        return identityProvider.getIdentity(request)
                .flatMap(id -> connectionDao.removeFromBlacklist(id, blacklistedUser))
                .filter(e -> e)
                .flatMap(e -> responseSupplier.noContent())
                .switchIfEmpty(responseSupplier.badRequest("Could not remove user from blacklist."))
                .onErrorResume(responseSupplier::error);
    }
}
