package com.romanenko.connection;

import reactor.core.publisher.Mono;

public interface UserConnectionCache {
    Mono<ConnectionType> getCachedConnectionType(String initiatorId, String otherPersonId);

    Mono<Void> saveConnection(String initiatorId, String otherPersonId, ConnectionType connectionType);

    Mono<Void> clearConnection(String initiatorId, String otherPersonId);

    Mono<Permission> getPermission(String initiatorId, String otherPersonId, PermissionKey permissionKey);

    Mono<Void> savePermission(String initiatorId, String otherPersonId, PermissionKey permissionKey, Permission value);
}
