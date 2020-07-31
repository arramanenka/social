package com.romanenko.handler

import com.romanenko.dao.GroupChatDao
import com.romanenko.io.ResponseSupplier
import com.romanenko.model.GroupChat
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
        private val groupChatDao: GroupChatDao,
        private val responseSupplier: ResponseSupplier,
        private val identityProvider: IdentityProvider
) : Routable {
    override fun declareRoute(builder: ApiBuilder) {
        builder
                .post("/chat/direct", ::saveDirectChat)
                .post("/chat/group", ::saveGroupChat)

                .delete("/chat/{chatId}", ::deleteChat)
                .get("/chats/direct", ::getDirectChats)
                .get("/chats/group", ::getGroupChats)

                .post("/chat/group/{chatId}/invitation/{userId}", ::inviteMember)
                .delete("/chat/group/{chatId}/invitation/{userId}", ::removeInvitation)
    }

    /**
     * Either create chat, or update chat name
     */
    private fun saveGroupChat(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap { identity ->
                    request.bodyToMono(GroupChat::class.java)
                            .switchIfEmpty(Mono.error<GroupChat>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Empty chat")))
                            .doOnSuccess { it.creatorId = identity.id }
                }.flatMap {
                    if (it.chatId == null) {
                        if (it.name.isNullOrBlank()) {
                            return@flatMap Mono.error<GroupChat>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Chat name is not specified"))
                        }
                        return@flatMap groupChatDao.createChat(it)
                    }
                    groupChatDao.updateChat(it)
                }
        return responseSupplier.ok(result, GroupChat::class.java)
    }

    private fun deleteChat(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMap { groupChatDao.deleteChat(it, request.pathVariable("chatId")) }
        return responseSupplier.ok(result, Void::class.java)
    }

    private fun getGroupChats(request: ServerRequest): Mono<ServerResponse> {
        val result = identityProvider.getIdentity(request)
                .flatMapMany { groupChatDao.getOwnChats(it) }
        return responseSupplier.ok(result, GroupChat::class.java)
    }

    private fun inviteMember(request: ServerRequest): Mono<ServerResponse> {
        val chatId = request.pathVariable("chatId")
        val userId = request.pathVariable("userId")
        val result = identityProvider.getIdentity(request)
                .flatMap {
                    if (it.id == userId) {
                        return@flatMap Mono.error<Void>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot add yourself as a member"))
                    }
                    groupChatDao.inviteMember(it, chatId, userId)
                }
        return responseSupplier.ok(result, Void::class.java)
    }

    private fun removeInvitation(request: ServerRequest): Mono<ServerResponse> {
        val chatId = request.pathVariable("chatId")
        val userId = request.pathVariable("userId")
        val result = identityProvider.getIdentity(request)
                .flatMap {
                    if (it.id == userId) {
                        return@flatMap Mono.error<Void>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot remove yourself from chat"))
                    }
                    groupChatDao.removeInvitation(it, chatId, userId)
                }
        return responseSupplier.ok(result, Void::class.java)
    }

}