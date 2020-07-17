package com.romanenko.uservice.dao;

import com.romanenko.security.Identity;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConnectionDao {
    Flux<String> getFollowersOfUser(Identity identity, String id);

    Flux<String> getFollowedByUser(Identity identity, String id);

    Mono<Boolean> addFollower(Identity id, String followingId);

    Mono<Boolean> removeFollower(Identity id, String followingId);

    Flux<String> getBlacklist(Identity identity);

    Mono<Boolean> blacklist(Identity id, String blacklistedUser);

    Mono<Boolean> removeFromBlacklist(Identity id, String blacklistedUser);
}
