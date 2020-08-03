package com.romanenko.connection.impl;

import com.romanenko.connection.ConnectionType;
import com.romanenko.connection.Permission;
import com.romanenko.connection.PermissionKey;
import com.romanenko.connection.UserConnectionCache;
import lombok.SneakyThrows;
import org.redisson.Redisson;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
//todo search for failsafe solution (retry on fail to connect)
public class RedisUserConnectionCache implements UserConnectionCache {

    private final RMapReactive<String, String> connections;
    private final RedissonReactiveClient client;
    private final String[] collectionKeys;

    @SneakyThrows
    public RedisUserConnectionCache() {
        Config config = Config.fromYAML(getClass().getResource("/redisConfig.yml"));
        this.client = Redisson.createReactive(config);
        this.connections = client.getMap("connections");
        Set<String> permissionKeysValues = Arrays.stream(PermissionKey.values()).map(PermissionKey::getKey).collect(Collectors.toSet());
        permissionKeysValues.add("connections");
        this.collectionKeys = permissionKeysValues.toArray(String[]::new);
    }

    @Override
    public Mono<ConnectionType> getCachedConnectionType(String initiatorId, String otherPersonId) {
        return connections.get(toKey(initiatorId, otherPersonId))
                .flatMap(name -> {
                    if (name == null) {
                        return Mono.empty();
                    }
                    return Mono.just(ConnectionType.forName(name));
                });
    }

    @Override
    public Mono<Void> saveConnection(String initiatorId, String otherPersonId, ConnectionType connectionType) {
        return connections.fastPut(toKey(initiatorId, otherPersonId), connectionType.name)
                .then();
    }

    @Override
    public Mono<Void> clearConnection(String initiatorId, String otherPersonId) {
        String key1 = toKey(initiatorId, otherPersonId);
        String key2 = toKey(otherPersonId, initiatorId);

        return Flux.fromArray(this.collectionKeys)
                .flatMap(k -> getCacheMap(k).fastRemove(key1, key2))
                .then();
    }

    @Override
    public Mono<Permission> getPermission(String initiatorId, String otherPersonId, PermissionKey permissionKey) {
        return getCacheMap(permissionKey.getKey()).get(toKey(initiatorId, otherPersonId))
                .flatMap(name -> {
                    if (name == null) {
                        return Mono.empty();
                    }
                    return Mono.just(Permission.forName(name));
                });
    }

    @Override
    public Mono<Void> savePermission(String initiatorId, String otherPersonId, PermissionKey permissionKey, Permission value) {
        return getCacheMap(permissionKey.getKey()).fastPut(toKey(initiatorId, otherPersonId), value.name())
                .then();
    }

    private RMapReactive<String, String> getCacheMap(String name) {
        return client.getMap(name);
    }

    private String toKey(String initiatorId, String otherPersonId) {
        return initiatorId + "..." + otherPersonId;
    }
}
