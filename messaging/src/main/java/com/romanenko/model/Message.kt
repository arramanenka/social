package com.romanenko.model

import lombok.ToString

@ToString
data class Message(
        var messageId: Int? = null,
        var senderId: String? = null,
        var text: String? = null,
        var chatId: Int? = null
)