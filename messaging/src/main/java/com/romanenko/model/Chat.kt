package com.romanenko.model

data class Chat(
        var chatId: String?,
        var creatorId: String?,
        var name: String?,
        var members: Set<String>?,
        var type: ChatType?
)