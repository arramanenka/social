package com.romanenko.uservice.dao.impl.neo;

import com.romanenko.uservice.dao.DirectConnectionDao;
import com.romanenko.uservice.dao.impl.connection.ConnectionType;
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
                .flatMap(e -> connectionRepo.getConnection(otherPersonId, initiatorId)
                        .map(ConnectionType::forName)
                        .map(e::combine));
    }
}
