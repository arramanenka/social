package com.romanenko.storage

import com.romanenko.io.ResponseSupplier
import com.romanenko.routing.ApiBuilder
import com.romanenko.routing.Routable
import com.romanenko.security.IdentityProvider
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.InetAddress
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@Component
class ImageHandler(
        private val responseSupplier: ResponseSupplier,
        private val identityProvider: IdentityProvider,
        @Value("\${server.port}")
        private val port: Int
) : Routable {
    override fun declareRoute(builder: ApiBuilder) {
        builder
                .post("/file", ::uploadImage)
                .get("/file/{filePath}", ::getFile)
    }

    private fun uploadImage(request: ServerRequest): Mono<ServerResponse> {
        val fileName = request.queryParam("fileName")
        val uploadResult = fileName
                .map { name ->
                    identityProvider.getIdentity(request)
                            .flatMap { identity ->
                                val filePath = Path.of("files", identity.id, name)
                                val file = filePath.toFile()
                                try {
                                    file.parentFile.mkdirs()
                                    file.createNewFile()
                                } catch (e: Exception) {
                                    return@flatMap Mono.error(HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                                }
                                val contents: Publisher<DataBuffer> = request.multipartData().map { it["file"] }
                                        .flatMapMany { Flux.fromIterable(it) }
                                        .filter { it is FilePart }
                                        .cast(FilePart::class.java)
                                        .flatMap { it.content() }
                                val path = URLEncoder.encode(filePath.toString(), StandardCharsets.UTF_8)
                                val resourceLink = "${InetAddress.getLoopbackAddress().hostName}:${port}/file/$path"

                                return@flatMap DataBufferUtils.write(
                                        contents,
                                        filePath,
                                        StandardOpenOption.WRITE
                                ).thenReturn(StoredFile(resourceLink))
                            }
                }
                .orElse(Mono.error(HttpClientErrorException(HttpStatus.BAD_REQUEST, "No file name specified")))
        return responseSupplier.ok(uploadResult, StoredFile::class.java)
    }

    private fun getFile(request: ServerRequest): Mono<ServerResponse> {
        return responseSupplier.ok(
                FileSystemResource(request.pathVariable("filePath"))
        )
    }
}

data class StoredFile(
        var resourceLink: String
)
