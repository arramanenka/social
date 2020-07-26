package com.romanenko.model

import lombok.ToString

@ToString
data class Message(var senderId: String?, var receiverId: String?, var message: String?)