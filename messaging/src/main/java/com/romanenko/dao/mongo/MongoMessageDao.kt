package com.romanenko.dao.mongo

import com.romanenko.dao.MessageDao
import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoMessageDao(
        private val messageRepo: MessageRepo
) : MessageDao {

    override fun sendMessage(message: Message): Mono<Message> {
        //always save as new message. disallow updates
        message.messageId = null
        return messageRepo.save(MongoMessage(message)).map { it.toModel() }
    }

    override fun deleteMessage(message: Message): Mono<Message> {
        return messageRepo.deleteByMessageIdAndSenderIdAndReceiverId(message.messageId!!, message.senderId!!, message.receiverId!!).map { it.toModel() }
    }

    override fun getMessages(queryingPerson: String, userId: String, pageQuery: PageQuery): Flux<Message> {
        return messageRepo.findAllMessagesBetweenUsers(queryingPerson, userId, pageQuery.calculateSkipAmount(), pageQuery.pageSize).map { it.toModel() }
    }
}