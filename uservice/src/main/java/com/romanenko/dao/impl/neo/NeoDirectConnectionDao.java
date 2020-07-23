package com.romanenko.dao.impl.neo;

import com.romanenko.dao.DirectConnectionDao;
import com.romanenko.dao.impl.connection.ConnectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
                .flatMap(e -> {
                    if (e.equals(ConnectionType.BLACKLIST)) {
                        return Mono.just(e);
                    }
                    return connectionRepo.getConnection(otherPersonId, initiatorId)
                            .map(ConnectionType::forName)
                            .map(e::combine);
                });
    }
}
