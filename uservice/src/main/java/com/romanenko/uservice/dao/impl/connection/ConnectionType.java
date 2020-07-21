package com.romanenko.uservice.dao.impl.connection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConnectionType {
    FRIEND(ConnectionType.FRIEND_NAME),
    FOLLOW(ConnectionType.FOLLOW_NAME),
    NONE(ConnectionType.NONE_NAME),
    BLACKLIST(ConnectionType.BLACKLIST_NAME),

    ;
    public static final String NONE_NAME = "none";
    public static final String CONNECTION_NAME = "USER_CONNECTION";
    public static final String CONNECTION_TYPE_LABEL = "type";
    public static final String FOLLOW_NAME = "follow";
    public static final String BLACKLIST_NAME = "follow";
    public static final String FRIEND_NAME = "friend";
    @Getter
    private final String name;
}
