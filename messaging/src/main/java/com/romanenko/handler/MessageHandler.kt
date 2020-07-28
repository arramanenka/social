package com.romanenko.handler

import com.romanenko.dao.MessageDao
import com.romanenko.io.ResponseSupplier
import com.romanenko.model.Message
import com.romanenko.routing.ApiBuilder
import com.romanenko.routing.Routable
import com.romanenko.security.IdentityProvider
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class MessageHandler(
        private val messageDao: MessageDao,
        private val responseSupplier: ResponseSupplier,
        private val identityProvider: IdentityProvider
) : Routable {
    override fun declareRoute(builder: ApiBuilder) {
        builder
                .post("/message/{chatId}", ::postMessage)
                .delete("/message/{chatId}/{messageId}", ::deleteMessage)
                .get("/messages/{chatId}", ::getMessages)
    }

    private fun postMessage(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap { identity ->
                    request.bodyToMono(Message::class.java)
                            .switchIfEmpty(Mono.error<Message>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Empty message")))
                            .doOnSuccess { message ->
                                message.senderId = identity.id
                                message.chatId = request.pathVariable("chatId")
                            }
                }.flatMap { message ->
                    if (message.text?.isBlank() != false) {
                        return@flatMap Mono.error<Message>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Message is blank"))
                    }
                    Mono.just(message)
                }.flatMap { message: Message -> messageDao.saveMessage(message) }
        return responseSupplier.questionable_ok(result, Message::class.java)
    }

    private fun deleteMessage(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap {
                    messageDao.deleteMessage(it, request.pathVariable("chatId"), request.pathVariable("messageId"))
                }
        return responseSupplier.questionable_ok(result, Message::class.java)
    }

    private fun getMessages(request: ServerRequest): Mono<ServerResponse> {
        val messages = identityProvider.getIdentity(request).flatMapMany {
            messageDao.getAllMessages(it, request.pathVariable("chatId"))
        }
        return responseSupplier.questionable_ok(messages, Message::class.java)
    }
}