package com.romanenko.service.impl

import com.romanenko.dao.MessageDao
import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import com.romanenko.security.Identity
import org.junit.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class DefaultMessageServiceTest {
    private val messageDaoMock = mock(MessageDao::class.java)
    private val messageService = DefaultMessageService(messageDaoMock)

    @Test
    fun `verify bad request when receiver not present`() {
        val message = Mono.just(Message(senderId = "a", text = "message text"))

        StepVerifier.create(messageService.sendMessage(message))
                .verifyErrorMatches {
                    if (it is HttpClientErrorException) {
                        return@verifyErrorMatches it.statusCode == HttpStatus.BAD_REQUEST
                    }
                    false
                }
    }

    @Test
    fun `verify bad request when sender not present`() {
        val message = Mono.just(Message(receiverId = "a", text = "message text"))

        StepVerifier.create(messageService.sendMessage(message))
                .verifyErrorMatches {
                    if (it is HttpClientErrorException) {
                        return@verifyErrorMatches it.statusCode == HttpStatus.BAD_REQUEST
                    }
                    false
                }
    }

    @Test
    fun `verify bad request with improper users`() {
        val message = Mono.just(Message(senderId = "a", receiverId = "a", text = "message text"))

        StepVerifier.create(messageService.sendMessage(message))
                .verifyErrorMatches {
                    if (it is HttpClientErrorException) {
                        return@verifyErrorMatches it.statusCode == HttpStatus.BAD_REQUEST
                    }
                    false
                }
    }

    @Test
    fun `verify bad request when text is not present`() {
        val message = Mono.just(Message(receiverId = "a", senderId = "b"))

        StepVerifier.create(messageService.sendMessage(message))
                .verifyErrorMatches {
                    if (it is HttpClientErrorException) {
                        return@verifyErrorMatches it.statusCode == HttpStatus.BAD_REQUEST
                    }
                    false
                }
    }

    @Test
    fun `verify send message delegates to message dao`() {
        val message = Message(senderId = "b", receiverId = "a", text = "message text")
        val messageMono = Mono.just(message)

        `when`(messageDaoMock.sendMessage(message)).thenReturn(Mono.just(message))

        StepVerifier.create(messageService.sendMessage(messageMono))
                .then { verify(messageDaoMock).sendMessage(message) }
                .expectNextCount(1)
                .expectComplete()
                .verify()
    }


    @Test
    fun `verify delete message results in bad request with improper users`() {
        val message = Message(receiverId = "a", senderId = "a", messageId = "id1")

        StepVerifier.create(messageService.deleteMessage(message))
                .verifyErrorMatches {
                    if (it is HttpClientErrorException) {
                        return@verifyErrorMatches it.statusCode == HttpStatus.BAD_REQUEST
                    }
                    false
                }
    }

    @Test
    fun `verify delete message results in not found without message id`() {
        val message = Message(receiverId = "a", senderId = "b")

        StepVerifier.create(messageService.deleteMessage(message))
                .verifyErrorMatches {
                    if (it is HttpClientErrorException) {
                        return@verifyErrorMatches it.statusCode == HttpStatus.NOT_FOUND
                    }
                    false
                }
    }

    @Test
    fun `verify delete message delegates to message dao`() {
        val message = Message(receiverId = "a", senderId = "b", messageId = "id1")

        `when`(messageDaoMock.deleteMessage(message)).thenReturn(Mono.just(message))

        StepVerifier.create(messageService.deleteMessage(message))
                .then { verify(messageDaoMock).deleteMessage(message) }
                .expectNextCount(1)
                .expectComplete()
                .verify()
    }

    @Test
    fun `verify get messags results in bad request with improper users`() {
        val pageQuery = PageQuery(0, 10)
        val identityMock = mock(Identity::class.java)
        `when`(identityMock.id).thenReturn("b")

        StepVerifier.create(messageService.getMessages(identityMock, "b", pageQuery))
                .verifyErrorMatches {
                    if (it is HttpClientErrorException) {
                        return@verifyErrorMatches it.statusCode == HttpStatus.BAD_REQUEST
                    }
                    false
                }
    }

    @Test
    fun `verify get messages delegates to message dao`() {
        val identityMock = mock(Identity::class.java)
        val pageQuery = PageQuery(0, 10)

        `when`(identityMock.id).thenReturn("a")
        `when`(messageDaoMock.getMessages("a", "b", pageQuery)).thenReturn(Flux.empty())

        StepVerifier.create(messageService.getMessages(identityMock, "b", pageQuery))
                .then { verify(messageDaoMock).getMessages("a", "b", pageQuery) }
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }
}