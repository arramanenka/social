package com.romanenko.model

import lombok.ToString
import java.util.*

@ToString
data class Message(
        var messageId: String? = null,
        var senderId: String? = null,
        var text: String? = null,
        var chatId: String? = null,
        var createdAt: Date? = null
)