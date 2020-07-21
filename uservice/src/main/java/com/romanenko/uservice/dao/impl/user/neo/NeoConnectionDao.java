package com.romanenko.uservice.dao.impl.user.neo;

import com.romanenko.security.Identity;
import com.romanenko.uservice.dao.ConnectionDao;
import com.romanenko.uservice.dao.DirectConnectionDao;
import com.romanenko.uservice.dao.impl.connection.ConnectionType;
import com.romanenko.uservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NeoConnectionDao implements ConnectionDao {

    private final NeoConnectionRepo connectionRepo;
    private final DirectConnectionDao directConnectionDao;

    @Override
    public Flux<User> getFollowersOfUser(Identity initiator, String id) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(id)) {
            return connectionRepo.getFollowers(initiatorId);
        }
        return directConnectionDao.getRelations(initiatorId, id)
                .flatMapMany(connectionType -> {
                    if (!connectionType.equals(ConnectionType.BLACKLIST)) {
                        return connectionRepo.getFollowers(id);
                    }
                    return Mono.error(new HttpClientErrorException(HttpStatus.FORBIDDEN));
                });
    }

    @Override
    public Flux<User> getFollowedByUser(Identity initiator, String id) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(id)) {
            return connectionRepo.getFollowing(initiatorId);
        }
        return directConnectionDao.getRelations(initiatorId, id)
                .flatMapMany(connectionType -> {
                    if (!connectionType.equals(ConnectionType.BLACKLIST)) {
                        return connectionRepo.getFollowing(id);
                    }
                    return Mono.error(new HttpClientErrorException(HttpStatus.FORBIDDEN));
                });
    }

    @Override
    public Mono<Void> addFollower(Identity initiator, String followingId) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(followingId)) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot follow thyself"));
        }
        return connectionRepo.follow(initiatorId, followingId);
    }

    @Override
    public Mono<Void> removeFollower(Identity initiator, String followingId) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(followingId)) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot follow thyself"));
        }
        return connectionRepo.unfollow(initiatorId, followingId);
    }

    @Override
    public Flux<User> getBlacklist(Identity identity) {
        return connectionRepo.getBlacklist(identity.getId());
    }

    @Override
    public Mono<Void> blacklist(Identity initiator, String blacklistedUser) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(blacklistedUser)) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot blacklist thyself"));
        }
        return connectionRepo.blacklist(initiatorId, blacklistedUser);
    }

    @Override
    public Mono<Void> removeFromBlacklist(Identity initiator, String blacklistedUser) {
        String initiatorId = initiator.getId();
        if (initiatorId.equals(blacklistedUser)) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot blacklist thyself"));
        }
        return connectionRepo.removeFromBlacklist(initiatorId, blacklistedUser);
    }
}
