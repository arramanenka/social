package com.romanenko.dao;

import com.romanenko.dao.impl.connection.ConnectionType;
import reactor.core.publisher.Mono;

public interface DirectConnectionDao {
    Mono<ConnectionType> getRelations(String initiatorId, String otherPersonId);

    Mono<Void> recalculateRelations(String initiatorId, String otherPersonId, ConnectionType connection);
}
