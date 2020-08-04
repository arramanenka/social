package com.romanenko.dao.cassandra

import com.datastax.oss.driver.api.core.uuid.Uuids
import com.romanenko.model.Message
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.io.Serializable
import java.util.*


@Table
class CassandraMessage(
        @PrimaryKey
        var messageKey: MessageKey,
        var text: String? = null,
        var messageId: UUID = Uuids.timeBased()
) {
    fun toModel(): Message = Message(messageId.toString(), messageKey.senderId, messageKey.receiverId, text, messageKey.createdAt)

    constructor(message: Message) : this(MessageKey(message), message.text)
}

@PrimaryKeyClass
@NoArgsConstructor
@EqualsAndHashCode
class MessageKey(
        @PrimaryKeyColumn(name = "senderId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        var senderId: String,
        @PrimaryKeyColumn(name = "receiverId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
        var receiverId: String,
        @PrimaryKeyColumn(name = "createdAt", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
        var createdAt: Date? = Date()
) : Serializable {
    constructor(message: Message) : this(message.senderId!!, message.receiverId!!)
}