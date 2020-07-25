package com.romanenko.connection.impl;

import com.romanenko.connection.ConnectionType;
import com.romanenko.connection.UserConnectionCache;
import lombok.SneakyThrows;
import org.redisson.Redisson;
import org.redisson.api.RMapReactive;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RedisUserConnectionCache implements UserConnectionCache {

    private final RMapReactive<String, String> connections;

    @SneakyThrows
    public RedisUserConnectionCache() {
        Config config = Config.fromYAML(getClass().getResource("/redisConfig.yml"));
        this.connections = Redisson.createReactive(config).getMap("connections");
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
        return connections.put(toKey(initiatorId, otherPersonId), connectionType.name)
                .then();
    }

    @Override
    public Mono<Void> clearConnection(String initiatorId, String otherPersonId) {
        return connections.fastRemove(toKey(initiatorId, otherPersonId), toKey(otherPersonId, initiatorId))
                .then();
    }

    private String toKey(String initiatorId, String otherPersonId) {
        return initiatorId + "..." + otherPersonId;
    }
}
