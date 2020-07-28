package com.romanenko.handler

import com.romanenko.dao.ChatDao
import com.romanenko.io.ResponseSupplier
import com.romanenko.model.Chat
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
class ChatHandler(
        private val chatDao: ChatDao,
        private val responseSupplier: ResponseSupplier,
        private val identityProvider: IdentityProvider
) : Routable {
    override fun declareRoute(builder: ApiBuilder) {
        builder
                .post("/chat", ::saveChat)
                .delete("/chat/{chatId}", ::deleteChat)
                .get("/chats", ::getOwnChats)

                .post("/chat/{chatId}/member/{userId}", ::addMember)
                .delete("/chat/{chatId}/member/{userId}", ::removeMember)
    }

    /**
     * Either create chat, or update chat name
     */
    private fun saveChat(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap { identity ->
                    request.bodyToMono(Chat::class.java)
                            .switchIfEmpty(Mono.error<Chat>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Empty chat")))
                            .doOnSuccess { it.creatorId = identity.id }
                }.flatMap {
                    if (it.chatId == null) {
                        if (it.type == null) {
                            return@flatMap Mono.error<Chat>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Chat type is not specified"))
                        } else if (it.name.isNullOrBlank()) {
                            return@flatMap Mono.error<Chat>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Chat name is not specified"))
                        }
                        return@flatMap chatDao.createChat(it)
                    }
                    chatDao.updateChat(it)
                }
        return responseSupplier.ok(result, Chat::class.java)
    }

    private fun deleteChat(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap { chatDao.deleteChat(it, request.pathVariable("chatId")) }
        return responseSupplier.ok(result, Void::class.java)
    }

    private fun getOwnChats(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMapMany { chatDao.getOwnChats(it) }
        return responseSupplier.ok(result, Chat::class.java)
    }

    private fun addMember(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap { chatDao.addMember(it, request.pathVariable("chatId"), request.pathVariable("userId")) }
        return responseSupplier.ok(result, Void::class.java)
    }

    private fun removeMember(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap { chatDao.removeMember(it, request.pathVariable("chatId"), request.pathVariable("userId")) }
        return responseSupplier.ok(result, Void::class.java)
    }

}