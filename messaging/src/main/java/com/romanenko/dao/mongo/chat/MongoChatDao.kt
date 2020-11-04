package com.romanenko.dao.mongo.chat

import com.romanenko.dao.ChatDao
import com.romanenko.model.Message
import com.romanenko.model.PrivateChat
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoChatDao(
        private val chatRepo: ChatRepo,
        private val reactiveMongoTemplate: ReactiveMongoTemplate
) : ChatDao {
    override fun getChats(ownerId: String): Flux<PrivateChat> {
        return chatRepo.findAllByOwnerId(ownerId, Sort.by(Sort.Direction.DESC, "lastMessageTime", "unreadCount"))
                .map { it.toModel() }
    }

    override fun getChat(id: String, interlocutorId: String): Mono<PrivateChat> {
        return chatRepo.findByOwnerIdAndInterlocutorId(id, interlocutorId)
                .map { it.toModel() }
    }

    override fun addLastMessageInfo(message: Message): Mono<Void> {
        val receiverChatQuery = queryChat(message.senderId!!, message.receiverId!!)
        val receiverUpdate = Update.update("lastMessageTime", message.createdAt)
                .inc("unreadCount", 1)
                .set("lastMessageText", message.text)

        val senderChatQuery = queryChat(message.receiverId!!, message.senderId!!)
        val senderUpdate = Update.update("lastMessageTime", message.createdAt)
                .set("unreadCount", 0)
                .set("lastMessageText", message.text)

        return reactiveMongoTemplate.upsert(receiverChatQuery, receiverUpdate, MongoChat::class.java).then(
                reactiveMongoTemplate.upsert(senderChatQuery, senderUpdate, MongoChat::class.java).then()
        )
    }

    override fun clearUnread(id: String, userId: String): Mono<Void> {
        val query = queryChat(userId, id)
        val upd = Update.update("unreadCount", 0)
        return reactiveMongoTemplate.updateFirst(query, upd, MongoChat::class.java).then()
    }

    override fun clearUnread(id: String, userId: String, amount: Int): Mono<Long> {
        val query = queryChat(userId, id)
        val upd = AggregationUpdate.update().set("unreadCount")
                .toValue(ConditionalOperators.switchCases(
                        ConditionalOperators.Switch.CaseOperator.`when`(ComparisonOperators.Gte.valueOf("unreadCount")
                                .greaterThanEqualToValue(amount))
                                .then(ArithmeticOperators.valueOf("unreadCount").subtract(amount)),
                        ConditionalOperators.Switch.CaseOperator.`when`(ComparisonOperators.Lte.valueOf("unreadCount")
                                .lessThanEqualToValue(amount))
                                .then(0)
                ))
        return reactiveMongoTemplate.findAndModify(query, upd, MongoChat::class.java)
                .map { it.unreadCount ?: 0 }
    }

    private fun queryChat(interlocutorId: String, ownerId: String): Query {
        return Query.query(Criteria
                .where("interlocutorId").`is`(interlocutorId)
                .and("ownerId").`is`(ownerId)
        )
    }
}
