package com.romanenko.model

import lombok.ToString

@ToString
data class Message(
        var senderId: String? = null,
        var message: String? = null,
        var messageId: String? = null,
        var chatId: Int? = null
)