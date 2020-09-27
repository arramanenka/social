package com.romanenko.dao.impl.neo;

import com.romanenko.connection.ConnectionType;
import com.romanenko.dao.ConnectionDao;
import com.romanenko.dao.DirectConnectionDao;
import com.romanenko.dao.impl.neo.model.NeoUser;
import com.romanenko.io.PageQuery;
import com.romanenko.model.User;
import com.romanenko.security.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NeoConnectionDao implements ConnectionDao {

    private final NeoConnectionRepository connectionRepo;
    private final DirectConnectionDao directConnectionDao;

    @Override
    public Flux<User> getFollowersOfUser(Identity initiator, String id, PageQuery pageQuery) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(id)) {
            return connectionRepo.getOwnFollowers(initiatorId, pageQuery.calculateSkipAmount(), pageQuery.pageSize)
                    .map(NeoUser::convertSimpleProfile);
        }
        return connectionRepo.getFollowers(initiatorId, id, pageQuery.calculateSkipAmount(), pageQuery.pageSize)
                .map(NeoUser::convertSimpleProfile);
    }

    @Override
    public Flux<User> getFollowedByUser(Identity initiator, String id, PageQuery pageQuery) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(id)) {
            return connectionRepo.getOwnFollowing(initiatorId, pageQuery.calculateSkipAmount(), pageQuery.pageSize)
                    .map(NeoUser::convertSimpleProfile);
        }
        return connectionRepo.getFollowing(initiatorId, id, pageQuery.calculateSkipAmount(), pageQuery.pageSize)
                .map(NeoUser::convertSimpleProfile);
    }

    @Override
    public Mono<Void> addFollower(Identity initiator, String followingId) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(followingId)) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot follow thyself"));
        }
        return connectionRepo.follow(initiatorId, followingId)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Could not find a user to follow")))
                .doOnSuccess(r -> directConnectionDao.recalculateRelations(initiatorId, followingId, ConnectionType.FOLLOW)
                        .subscribeOn(Schedulers.parallel()).subscribe())
                .then();
    }

    @Override
    public Mono<Void> removeFollower(Identity initiator, String followingId) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(followingId)) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot follow thyself"));
        }
        return connectionRepo.unfollow(initiatorId, followingId)
                .doOnSuccess(r -> directConnectionDao.recalculateRelations(initiatorId, followingId, ConnectionType.NONE)
                        .subscribeOn(Schedulers.parallel()).subscribe());
    }

    @Override
    public Flux<User> getBlacklist(Identity identity, PageQuery pageQuery) {
        return connectionRepo.getBlacklist(identity.getId(), pageQuery.calculateSkipAmount(), pageQuery.pageSize)
                .map(NeoUser::convertSimpleProfile);
    }

    @Override
    public Mono<Void> blacklist(Identity initiator, String blacklistedUser) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(blacklistedUser)) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot blacklist thyself"));
        }
        return connectionRepo.blacklist(initiatorId, blacklistedUser)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Could not find a user to block")))
                .doOnSuccess(r -> directConnectionDao.recalculateRelations(initiatorId, blacklistedUser, ConnectionType.BLACKLIST)
                        .subscribeOn(Schedulers.parallel()).subscribe())
                .then();
    }

    @Override
    public Mono<Void> removeFromBlacklist(Identity initiator, String blacklistedUser) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(blacklistedUser)) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot blacklist thyself"));
        }
        return connectionRepo.removeFromBlacklist(initiatorId, blacklistedUser)
                .doOnSuccess(r -> directConnectionDao.recalculateRelations(initiatorId, blacklistedUser, ConnectionType.NONE)
                        .subscribeOn(Schedulers.parallel()).subscribe());
    }
}
