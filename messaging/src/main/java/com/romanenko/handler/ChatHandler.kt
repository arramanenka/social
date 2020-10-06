package com.romanenko.handler

import com.romanenko.io.ResponseSupplier
import com.romanenko.model.PrivateChat
import com.romanenko.routing.ApiBuilder
import com.romanenko.routing.Routable
import com.romanenko.security.IdentityProvider
import com.romanenko.service.ChatService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class ChatHandler(
        private val responseSupplier: ResponseSupplier,
        private val identityProvider: IdentityProvider,
        private val chatService: ChatService
) : Routable {

    override fun declareRoute(builder: ApiBuilder) {
        builder
                .get("/chats", ::getChats)
                .get("/chat/{interlocutorId}", ::getChat)
    }

    private fun getChat(request: ServerRequest): Mono<ServerResponse> {
        val chat = identityProvider.getIdentity(request)
                .flatMap {
                    chatService.getChat(it.id, request.pathVariable("interlocutorId"))
                }
        return responseSupplier.ok(chat, PrivateChat::class.java)
    }

    private fun getChats(request: ServerRequest): Mono<ServerResponse> {
        val chats = identityProvider.getIdentity(request)
                .flatMapMany {
                    chatService.getChats(it.id)
                }
        return responseSupplier.ok(chats, PrivateChat::class.java)
    }
}
