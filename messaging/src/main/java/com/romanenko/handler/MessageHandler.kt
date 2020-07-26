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
        builder.post("/message/{userId}", ::postMessage)
                .delete("/message/{userId}/{messageId}", ::deleteMessage)
                .get("/messages/{userId}", ::getMessages)
    }

    private fun postMessage(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap { identity ->
                    request.bodyToMono(Message::class.java)
                            .switchIfEmpty(Mono.error<Message>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Empty message")))
                            .doOnNext { message ->
                                println(message)
                                message.senderId = identity.id
                                message.receiverId = request.pathVariable("userId")
                            }
                }.flatMap { message ->
                    if (message.message?.isBlank() != false) {
                        return@flatMap Mono.error<Message>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Message is blank"))
                    }
                    Mono.just(message)
                }.flatMap { message: Message -> messageDao.saveMessage(message) }
        return responseSupplier.questionable_ok(result, Message::class.java)
    }

    private fun deleteMessage(request: ServerRequest): Mono<ServerResponse> {
        TODO("Not yet implemented")
    }

    private fun getMessages(request: ServerRequest): Mono<ServerResponse> {
        TODO("Not yet implemented")
    }
}