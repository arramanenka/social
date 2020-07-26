package com.romanenko.dao;

import com.romanenko.io.PageQuery;
import com.romanenko.model.User;
import com.romanenko.security.Identity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConnectionDao {
    Flux<User> getFollowersOfUser(Identity initiator, String id, PageQuery pageQuery);

    Flux<User> getFollowedByUser(Identity initiator, String id, PageQuery pageQuery);

    Mono<Void> addFollower(Identity initiator, String followingId);

    Mono<Void> removeFollower(Identity initiator, String followingId);

    Flux<User> getBlacklist(Identity identity, PageQuery pageQuery);

    Mono<Void> blacklist(Identity initiator, String blacklistedUser);

    Mono<Void> removeFromBlacklist(Identity initiator, String blacklistedUser);
}
