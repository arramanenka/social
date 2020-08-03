package com.romanenko.connection


enum class PermissionKey(val key: String) {
    MESSAGE("message"),


    ;

    companion object {
        @JvmStatic
        fun forName(name: String): PermissionKey? {
            for (value in values()) {
                if (value.key.contentEquals(name)) {
                    return value
                }
            }
            return null
        }
    }
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
