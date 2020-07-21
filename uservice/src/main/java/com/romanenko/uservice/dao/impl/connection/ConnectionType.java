package com.romanenko.uservice.dao.impl.connection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConnectionType {
    FOLLOW("follow"),
    BLACKLIST("blacklist"),
    ;
    @Getter
    private final String name;
}
