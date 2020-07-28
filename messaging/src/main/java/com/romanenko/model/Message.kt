package com.romanenko.model

import lombok.ToString
import java.util.*

@ToString
data class Message(
        var messageId: Int? = null,
        var senderId: String? = null,
        var text: String? = null,
        var chatId: Int? = null,
        var createdAt: Date? = null
)