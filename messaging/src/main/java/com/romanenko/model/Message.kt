package com.romanenko.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

data class Message(
        var messageId: String? = null,
        var senderId: String? = null,
        var receiverId: String? = null,
        var text: String? = null,
        var createdAt: Date? = null
) {

    @JsonIgnore
    fun areUsersNotValid(): Boolean {
        return senderId == null || receiverId == null || senderId.equals(receiverId)
    }

    @JsonIgnore
    fun isTextNotValid(): Boolean {
        return text?.isBlank() ?: true
    }
}
