package com.romanenko.model

import java.time.LocalDate

data class PrivateChat(
        var ownerId: String?,
        var interlocutorId: String?,
        var lastMessage: LocalDate?,
        var unreadCount: Long?,
        var lastMessageText: String?
)
