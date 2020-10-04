package com.romanenko;

import com.romanenko.connection.UserConnectionCache;
import com.romanenko.model.User;
import com.romanenko.security.IdentityProvider;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureWebTestClient
@EnableAutoConfiguration(exclude = ReactiveSecurityAutoConfiguration.class)
@ContextConfiguration(initializers = IntegrationTest.Initializer.class)
@Testcontainers
public class IntegrationTest {
    @Container
    private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>(DockerImageName.parse("neo4j:4.1.1"));
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private UserConnectionCache connectionCache;
    @MockBean
    private IdentityProvider identityProvider;

    private final User queryingUser = User.builder().build();

    @BeforeEach
    public void befE() {
        Mockito.when(identityProvider.getIdentity(any())).thenReturn(Mono.just(queryingUser::getId));
    }

    @Test
    public void testSaveRetrieve() {
        final var testId = "0testId";
        Mockito.when(identityProvider.getIdentity(any())).thenReturn(Mono.just(() -> testId));
        User user = User.builder()
                .name("alex123")
                .bio("Smth about myself")
                .build();
        webClient.post()
                .uri("/user")
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(testId)
                .jsonPath("bio").isEqualTo(user.getBio())
                .jsonPath("name").isEqualTo(user.getName());

        webClient.get()
                .uri("/user/" + testId)
                .exchange()
                .expectBody()
                .jsonPath("id").isEqualTo(testId)
                .jsonPath("bio").isEqualTo(user.getBio())
                .jsonPath("name").isEqualTo(user.getName())
                .jsonPath("followersAmount").isEqualTo(0)
                .jsonPath("followingAmount").isEqualTo(0);
        webClient.delete()
                .uri("/user")
                .exchange()
                .expectStatus().isOk();
        webClient.get()
                .uri("/user/" + testId)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    // Oh, almighty Satan, may I be forgiven for this abomination of test
    public void testNicknameRetrieve() {
        queryingUser.setName("a");
        queryingUser.setId("1a");
        saveUser(queryingUser);

        queryingUser.setName("aa");
        queryingUser.setId("1b");
        saveUser(queryingUser);

        queryingUser.setName("aaa");
        queryingUser.setId("1c");
        saveUser(queryingUser);

        queryingUser.setName("ea");
        queryingUser.setId("1e");
        saveUser(queryingUser);

        var expectedNames = new ArrayList<>() {{
            add("a");
            add("aa");
            add("aaa");
        }};

        Flux<User> users = webClient.get()
                .uri("/users/nickname/a")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(User.class)
                .getResponseBody();

        StepVerifier.create(users)
                .expectNextMatches(u -> expectedNames.remove(u.getName()))
                .expectNextMatches(u -> expectedNames.remove(u.getName()))
                .expectNextMatches(u -> expectedNames.remove(u.getName()))
                .verifyComplete();
    }

    @Test
    public void testFollowingConnectionCountsAndRetrieval() {

    }

    private void saveUser(User user) {
        webClient.post()
                .uri("/user")
                .bodyValue(user)
                .exchange();
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
