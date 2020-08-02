package com.romanenko.model

data class Message(
        var messageId: String? = null,
        var senderId: String? = null,
        var receiverId: String? = null,
        var text: String? = null
) {
    fun areUsersValid(): Boolean {
        return senderId != receiverId
    }

    fun isTextValid(): Boolean {
        return text?.isNotBlank() ?: false
    }
}