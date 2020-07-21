package com.romanenko.uservice.dao.impl.connection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConnectionType {
    FOLLOW(ConnectionType.FOLLOW_NAME) {
    },
    BLACKLIST(ConnectionType.BLACKLIST_NAME),

    ;
    public static final String FOLLOW_NAME = "follow";
    public static final String BLACKLIST_NAME = "follow";
    @Getter
    private final String name;
}
