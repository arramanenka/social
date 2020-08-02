package com.romanenko.service.impl

import com.romanenko.dao.MessageDao
import com.romanenko.model.Message
import org.junit.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class DefaultMessageServiceTest {
    private val messageDaoMock = mock(MessageDao::class.java)
    private val messageService = DefaultMessageService(messageDaoMock)

    @Test
    fun testSendMessageReceiverNotPresent() {
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
    fun testSendMessageSenderNotPresent() {
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
    fun testSendMessageWithImproperUsers() {
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
    fun testSendMessageWithoutText() {
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
    fun testSendMessageDelegatesMessageToDao() {
        val message = Mono.just(Message(senderId = "b", receiverId = "a", text = "message text"))
        `when`(messageDaoMock.sendMessage(message)).thenReturn(message)
        StepVerifier.create(messageService.sendMessage(message))
                .then { verify(messageDaoMock).sendMessage(message) }
                .expectNextCount(1)
                .expectComplete()
                .verify()
    }
}