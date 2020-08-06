package com.romanenko.io

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

fun <T> ServerRequest.safeBodyToMono(clazz: Class<T>): Mono<T> {
    return this.bodyToMono(clazz)
            .onErrorResume(NullPointerException::class.java) {
                Mono.error<T>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Incomplete request body."))
            }
}