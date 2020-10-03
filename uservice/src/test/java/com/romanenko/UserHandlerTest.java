package com.romanenko;

import com.romanenko.connection.UserConnectionCache;
import com.romanenko.security.Identity;
import com.romanenko.security.IdentityProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureWebTestClient
@EnableAutoConfiguration(exclude = ReactiveSecurityAutoConfiguration.class)
public class UserHandlerTest {
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private UserConnectionCache connectionCache;
    @MockBean
    private IdentityProvider identityProvider;

    @Test
    public void t() {
        Mockito.when(identityProvider.getIdentity(any())).thenAnswer(
                invocationOnMock -> {
                    ServerRequest request = invocationOnMock.getArgument(0, ServerRequest.class);
                    return Mono.just((Identity) () -> request.queryParam("id").orElse("id"));
                }
        );
        webClient.get()
                .uri("/users/nickname/a?id=a").exchange().expectStatus().isOk();
    }
}
