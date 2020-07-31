package com.romanenko.model

data class GroupChat(
        var chatId: String?,
        var creatorId: String?,
        var name: String?,
        var invitedMembers: Set<String>?,
)