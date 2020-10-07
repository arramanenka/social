package com.romanenko.dao.mongo

import com.romanenko.dao.MessageDao
import com.romanenko.dao.domain.OffsetLimitPageable
import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import org.bson.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
@ConditionalOnProperty(name = ["message.storage"], havingValue = "mongo", matchIfMissing = true)
class MongoMessageDao(
        private val messageRepo: MessageRepo,
        private val mongoTemplate: ReactiveMongoTemplate
) : MessageDao, ApplicationListener<ApplicationStartedEvent> {

    override fun sendMessage(message: Message): Mono<Message> {
        //always save as new message. disallow updates
        message.messageId = null
        return messageRepo.save(MongoMessage(message)).map { it.toModel() }
    }

    override fun deleteMessage(message: Message): Mono<Message> {
        return messageRepo.deleteByMessageIdAndSenderIdAndReceiverId(message.messageId!!, message.senderId!!, message.receiverId!!).map { it.toModel() }
    }

    override fun getMessages(queryingPerson: String, userId: String, pageQuery: PageQuery): Flux<Message> {
        val pageable = OffsetLimitPageable(pageQuery.skipAmount, pageQuery.amount, Sort.by(Sort.Direction.DESC, "createdAt"))
        return messageRepo.findAllMessagesBetweenUsers(queryingPerson, userId, pageable)
                .map { it.toModel() }
    }

    //todo check why compoundIndex for reactive mongo does not work by itself
    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        //do not start until index is ensured
        mongoTemplate.indexOps(MongoMessage::class.java)
                .ensureIndex(CompoundIndexDefinition(
                        Document.parse("{'senderId' : 1, 'receiverId' : 1}")
                )).block()
    }
}
