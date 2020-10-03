package com.romanenko;

import com.romanenko.connection.UserConnectionCache;
import com.romanenko.security.IdentityProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureWebTestClient
@EnableAutoConfiguration(exclude = ReactiveSecurityAutoConfiguration.class)
@ContextConfiguration(initializers = UserHandlerTest.Initializer.class)
@Testcontainers
public class UserHandlerTest {
    @Container
    private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>(DockerImageName.parse("neo4j:4.1.1"));
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private UserConnectionCache connectionCache;
    @MockBean
    private IdentityProvider identityProvider;

    @Test
    public void t() {
        Mockito.when(identityProvider.getIdentity(any())).thenReturn(Mono.just(() -> "id"));
        webClient.get()
                .uri("/users/nickname/a?id=a").exchange().expectStatus().isOk();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "org.neo4j.driver.uri=" + neo4jContainer.getBoltUrl(),
                    "org.neo4j.driver.authentication.username=neo4j",
                    "org.neo4j.driver.authentication.password=" + neo4jContainer.getAdminPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
