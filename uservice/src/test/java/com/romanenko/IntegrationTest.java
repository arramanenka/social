package com.romanenko;

import com.romanenko.connection.UserConnectionCache;
import com.romanenko.model.User;
import com.romanenko.security.IdentityProvider;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@Log4j2
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

    private User queryingUser;

    @BeforeEach
    public void setUp() {
        queryingUser = User.builder().build();
        Mockito.when(identityProvider.getIdentity(any())).thenReturn(Mono.just(queryingUser::getId));
        Mockito.when(connectionCache.clearConnection(anyString(), anyString())).thenReturn(Mono.empty());
        Mockito.when(connectionCache.getCachedConnectionType(anyString(), anyString())).thenReturn(Mono.empty());
    }

    @Test
    public void testSaveRetrieve() {
        queryingUser.setId("0testId");
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
                .jsonPath("id").isEqualTo(queryingUser.getId())
                .jsonPath("bio").isEqualTo(user.getBio())
                .jsonPath("name").isEqualTo(user.getName());

        webClient.get()
                .uri("/user/" + queryingUser.getId())
                .exchange()
                .expectBody()
                .jsonPath("id").isEqualTo(queryingUser.getId())
                .jsonPath("bio").isEqualTo(user.getBio())
                .jsonPath("name").isEqualTo(user.getName())
                .jsonPath("followersAmount").isEqualTo(0)
                .jsonPath("followingAmount").isEqualTo(0);
        webClient.delete()
                .uri("/user")
                .exchange()
                .expectStatus().isOk();
        webClient.get()
                .uri("/user/" + queryingUser.getId())
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
        saveUser("2a");
        saveUser("2b");
        saveUser("2c");
        saveUser("2d");
        saveUser("2e");
        log.info("Saved 5 users");
        // 2a -FOLLOW> 2b
        follow("2a", "2b");
        // 2a <-FOLLOW-> 2c
        follow("2a", "2c");
        follow("2c", "2a");
        // 2a <-FOLLOW- 2d
        follow("2d", "2a");
        log.info("Added follow connections");
        getVerifyUserFollowingAndFollowers(
                User.builder()
                        .id("2a")
                        .followingAmount(2)
                        .followersAmount(2)
                        .build(),
                "2a",
                new String[]{"2d", "2c"},
                new String[]{"2b", "2c"}
        );
        log.info("Verified 2a's connections");
        getVerifyUserFollowingAndFollowers(
                User.builder()
                        .id("2b")
                        .followingAmount(0)
                        .followersAmount(1)
                        .build(),
                "2b",
                new String[]{"2a"},
                new String[0]
        );
        log.info("Verified 2c's connections");
        getVerifyUserFollowingAndFollowers(
                User.builder()
                        .id("2c")
                        .followingAmount(1)
                        .followersAmount(1)
                        .build(),
                "2c",
                new String[]{"2a"},
                new String[]{"2a"}
        );
        log.info("Verified 2d's connections");
        getVerifyUserFollowingAndFollowers(
                User.builder()
                        .id("2d")
                        .followingAmount(1)
                        .followersAmount(0)
                        .build(),
                "2d",
                new String[0],
                new String[]{"2a"}
        );
        log.info("Verified 2e's connections");
        getVerifyUserFollowingAndFollowers(
                User.builder()
                        .id("2e")
                        .followingAmount(0)
                        .followersAmount(0)
                        .build(),
                "2e",
                new String[0],
                new String[0]
        );
    }

    private void getVerifyUserFollowingAndFollowers(
            User user, String queryingPerson, String[] followers, String[] following
    ) {
        queryingUser.setId(queryingPerson);

        webClient.get()
                .uri("/user/" + user.getId())
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(user.getId())
                .jsonPath("followingAmount").isEqualTo(user.getFollowingAmount())
                .jsonPath("followersAmount").isEqualTo(user.getFollowersAmount());

        verifyConnections(user, "/followers", followers);
        verifyConnections(user, "/following", following);
    }

    private void verifyConnections(User user, String path, String[] expectedUsers) {
        var expected = new ArrayList<>(List.of(expectedUsers));
        Flux<User> users = webClient.get()
                .uri("/connections/" + user.getId() + path)
                .exchange().expectStatus().isOk()
                .returnResult(User.class).getResponseBody();

        StepVerifier.create(users)
                .thenConsumeWhile(u -> expected.remove(u.getId()))
                .verifyComplete();
        Assert.assertTrue(expected.isEmpty());
    }

    private void follow(String followerId, String personId) {
        queryingUser.setId(followerId);
        webClient.post()
                .uri("/connections/follower/" + personId)
                .exchange()
                .expectStatus().isOk();
    }

    private void saveUser(String id) {
        queryingUser.setName(id);
        queryingUser.setId(id);
        saveUser(queryingUser);
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