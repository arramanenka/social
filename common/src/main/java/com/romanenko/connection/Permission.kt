package com.romanenko.connection


enum class PermissionKey(val key: String) {
    MESSAGE("message"),
    ;
}

enum class Permission {
    GRANTED,
    DENIED,

    /**
     * Blocked permission can be used to indicate DENIED for all permission keys
     */
    BLOCKED,
    ;

    companion object {
        @JvmStatic
        fun forName(name: String): Permission {
            for (value in values()) {
                if (value.name.contentEquals(name)) {
                    return value
                }
            }
            return DENIED
        }
    }
}
