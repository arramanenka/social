package com.romanenko.connection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConnectionType {
    FRIEND(ConnectionType.FRIEND_NAME, 3),
    FOLLOW(ConnectionType.FOLLOW_NAME, 2),
    NONE(ConnectionType.NONE_NAME, 1),
    BLACKLIST(ConnectionType.BLACKLIST_NAME, 0),

    ;
    public static final String NONE_NAME = "none";
    public static final String CONNECTION_NAME = "USER_CONNECTION";
    public static final String CONNECTION_TYPE_LABEL = "type";
    public static final String FOLLOW_NAME = "follow";
    public static final String BLACKLIST_NAME = "blacklist";
    public static final String FRIEND_NAME = "friend";
    public final String name;
    private final int numberRepresentation;

    public static ConnectionType forName(String name) {
        for (ConnectionType value : ConnectionType.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return NONE;
    }

    public ConnectionType combine(ConnectionType incomingConnection) {
        //quick check for blacklist
        if (incomingConnection.numberRepresentation * this.numberRepresentation == 0) {
            return ConnectionType.BLACKLIST;
        }
        if (this.equals(FOLLOW) && incomingConnection.numberRepresentation >= FOLLOW.numberRepresentation) {
            return FRIEND;
        }
        //otherwise nothing changes
        return this;
    }
}
