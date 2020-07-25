package com.romanenko.dao.impl.connection;

import org.junit.jupiter.api.Test;

import static com.romanenko.dao.impl.connection.ConnectionType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectionTypeTest {

    @Test
    public void testCombineBlacklists() {
        for (ConnectionType connectionType : ConnectionType.values()) {
            assertEquals(BLACKLIST.combine(connectionType), BLACKLIST);
        }
        for (ConnectionType connectionType : ConnectionType.values()) {
            assertEquals(connectionType.combine(BLACKLIST), BLACKLIST);
        }
    }

    @Test
    public void testCombineFollowerWithNone() {
        assertEquals(FOLLOW.combine(NONE), FOLLOW);
    }

    @Test
    public void testCombineFollowerWithFollower() {
        assertEquals(FOLLOW.combine(FOLLOW), FRIEND);
    }

    @Test
    public void testCombineFollowerWithFriend() {
        assertEquals(FOLLOW.combine(FRIEND), FRIEND);
    }

    @Test
    public void testCombineFriendWithFollower() {
        assertEquals(FRIEND.combine(FOLLOW), FRIEND);
    }

    @Test
    public void testCombineNoneResultsInNone() {
        for (ConnectionType value : values()) {
            if (value.equals(BLACKLIST)) {
                continue;
            }
            assertEquals(NONE.combine(value), NONE);
        }
    }

}