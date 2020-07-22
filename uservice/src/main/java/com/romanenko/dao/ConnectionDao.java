package com.romanenko.dao;

import com.romanenko.security.Identity;
import com.romanenko.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConnectionDao {
    Flux<User> getFollowersOfUser(Identity initiator, String id);

    Flux<User> getFollowedByUser(Identity initiator, String id);

    Mono<Void> addFollower(Identity initiator, String followingId);

    Mono<Void> removeFollower(Identity initiator, String followingId);

    Flux<User> getBlacklist(Identity identity);

    Mono<Void> blacklist(Identity initiator, String blacklistedUser);

    Mono<Void> removeFromBlacklist(Identity initiator, String blacklistedUser);
}
