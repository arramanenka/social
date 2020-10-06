package com.romanenko.model

import java.util.*

data class PrivateChat(
        var ownerId: String?,
        var interlocutorId: String?,
        var lastMessage: Date?,
        var unreadCount: Long?,
        var lastMessageText: String?
)
