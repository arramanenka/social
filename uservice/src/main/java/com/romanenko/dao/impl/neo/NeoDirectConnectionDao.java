package com.romanenko.dao.impl.neo;

import com.romanenko.connection.ConnectionType;
import com.romanenko.connection.UserConnectionCache;
import com.romanenko.dao.DirectConnectionDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class NeoDirectConnectionDao implements DirectConnectionDao {
    private final NeoConnectionRepo connectionRepo;
    private final UserConnectionCache connectionCache;

    @Override
    public Mono<ConnectionType> getRelations(String initiatorId, String otherPersonId) {
        return connectionCache.getCachedConnectionType(initiatorId, otherPersonId)
                .switchIfEmpty(getNonCachedRelations(initiatorId, otherPersonId));
    }

    private Mono<ConnectionType> getNonCachedRelations(String initiatorId, String otherPersonId) {
        return connectionRepo.getConnection(initiatorId, otherPersonId)
                .map(ConnectionType::forName)
                .switchIfEmpty(Mono.just(ConnectionType.NONE))
                .flatMap(e -> {
                    if (e.equals(ConnectionType.BLACKLIST)) {
                        return Mono.just(e);
                    }
                    return connectionRepo.getConnection(otherPersonId, initiatorId)
                            .map(ConnectionType::forName)
                            .map(e::combine)
                            .switchIfEmpty(Mono.just(ConnectionType.NONE));
                })
                .doOnSuccess(e -> connectionCache.saveConnection(initiatorId, otherPersonId, e)
                        .subscribeOn(Schedulers.parallel())
                        .subscribe()
                );
    }

    @Override
    public Mono<Void> recalculateRelations(String initiatorId, String otherPersonId, ConnectionType connection) {
        return connectionCache.clearConnection(initiatorId, otherPersonId);
    }
}
