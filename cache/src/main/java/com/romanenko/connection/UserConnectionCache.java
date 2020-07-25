package com.romanenko.connection;

import reactor.core.publisher.Mono;

public interface UserConnectionCache {
    Mono<ConnectionType> getCachedConnectionType(String initiatorId, String otherPersonId);

    Mono<Void> saveConnection(String initiatorId, String otherPersonId, ConnectionType connectionType);

    Mono<Void> clearConnection(String initiatorId, String otherPersonId);
}
