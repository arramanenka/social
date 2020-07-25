package com.romanenko.dao.impl.neo;

import com.romanenko.dao.DirectConnectionDao;
import com.romanenko.connection.ConnectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NeoDirectConnectionDao implements DirectConnectionDao {
    private final NeoConnectionRepo connectionRepo;

    //TODO add cache via redis
    @Override
    public Mono<ConnectionType> getRelations(String initiatorId, String otherPersonId) {
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
                });
    }

    @Override
    public Mono<Void> recalculateRelations(String initiatorId, String otherPersonId, ConnectionType connection) {
        return Mono.error(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "not implemented"));
    }
}
