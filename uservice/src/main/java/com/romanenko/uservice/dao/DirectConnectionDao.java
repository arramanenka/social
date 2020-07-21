package com.romanenko.uservice.dao;

import com.romanenko.uservice.dao.impl.connection.ConnectionType;
import reactor.core.publisher.Mono;

public interface DirectConnectionDao {
    Mono<ConnectionType> getRelations(String initiatorId, String otherPersonId);
}
