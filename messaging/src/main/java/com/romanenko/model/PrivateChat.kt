package com.romanenko.model

import java.time.LocalDate

data class PrivateChat(
        val ownerId: String,
        val interlocutorId: String,
        val lastMessage: LocalDate,
        val unreadCount: Long
)
