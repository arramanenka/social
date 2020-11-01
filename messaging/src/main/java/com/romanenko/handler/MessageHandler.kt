package com.romanenko.handler

import com.romanenko.io.PageQuery
import com.romanenko.io.ResponseSupplier
import com.romanenko.io.safeBodyToMono
import com.romanenko.model.Message
import com.romanenko.routing.ApiBuilder
import com.romanenko.routing.Routable
import com.romanenko.security.IdentityProvider
import com.romanenko.service.MessageService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class MessageHandler(
        private val responseSupplier: ResponseSupplier,
        private val identityProvider: IdentityProvider,
        private val messageService: MessageService
) : Routable {
    override fun declareRoute(builder: ApiBuilder) {
        builder
                .post("/message/{userId}", ::sendMessage)
                .delete("/message/{userId}/{messageId}", ::deleteMessage)
                .get("/messages/{userId}", ::getMessages)
                .get("/messages/{userId}/unread", ::getUnread)
    }

    private fun sendMessage(request: ServerRequest): Mono<ServerResponse> {
        val message = identityProvider.getIdentity(request)
                .flatMap {
                    request.safeBodyToMono(Message::class.java)
                            .doOnNext { m ->
                                m.senderId = it.id
                                m.receiverId = request.pathVariable("userId")
                            }
                }
        return responseSupplier.ok(messageService.sendMessage(message), Message::class.java)
    }

    private fun deleteMessage(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        val messageId = request.pathVariable("messageId")
        val message = identityProvider.getIdentity(request)
                .flatMap {
                    messageService.deleteMessage(Message(messageId, it.id, userId))
                }
        return responseSupplier.ok(message, Message::class.java)
    }

    private fun getMessages(request: ServerRequest): Mono<ServerResponse> {
        val messages = identityProvider.getIdentity(request)
                .flatMapMany { messageService.getMessages(it, request.pathVariable("userId"), PageQuery(request)) }
        return responseSupplier.ok(messages, Message::class.java)
    }

    private fun getUnread(request: ServerRequest): Mono<ServerResponse> {
        val messages = identityProvider.getIdentity(request)
                .flatMapMany { messageService.getUnread(it, request.pathVariable("userId"), PageQuery(request)) }
        return responseSupplier.ok(messages, Message::class.java)
    }
}
