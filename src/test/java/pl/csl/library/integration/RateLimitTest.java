package pl.csl.library.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "app.ratelimit.auth-per-minute=3",
        "app.ratelimit.anon-per-minute=2"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RateLimitIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    TestRestTemplate rest;

    @Test
    void anonymous_search_should_eventually_return_429() {
        ResponseEntity<String> r1 = rest.getForEntity("/api/books/search?q=harry&page=0&size=1", String.class);
        ResponseEntity<String> r2 = rest.getForEntity("/api/books/search?q=harry&page=0&size=1", String.class);
        ResponseEntity<String> r3 = rest.getForEntity("/api/books/search?q=harry&page=0&size=1", String.class);
        if (r1.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            assertThat(r2.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(r3.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            return;
        }

        assertThat(r1.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(r2.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(r3.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(r3.getHeaders().getFirst("Retry-After")).isNotBlank();
    }

    @Test
    void authenticated_search_should_eventually_return_429() {
        String token = registerAndLoginGetToken("test@test.com", "test");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> r1 = rest.exchange(
                "/api/books/search?q=harry&page=0&size=1",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        ResponseEntity<String> r2 = rest.exchange(
                "/api/books/search?q=harry&page=0&size=1",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        ResponseEntity<String> r3 = rest.exchange(
                "/api/books/search?q=harry&page=0&size=1",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        ResponseEntity<String> r4 = rest.exchange(
                "/api/books/search?q=harry&page=0&size=1",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertThat(r1.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(r2.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(r3.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(r4.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        assertThat(r4.getHeaders().getFirst("Retry-After")).isNotBlank();
    }

    private String registerAndLoginGetToken(String email, String password) {
        ResponseEntity<Void> reg = rest.postForEntity(
                "/api/auth/register",
                Map.of("email", email, "password", password),
                Void.class
        );
        assertThat(reg.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.CONFLICT);

        ResponseEntity<Map> login = rest.postForEntity(
                "/api/auth/login",
                Map.of("email", email, "password", password),
                Map.class
        );
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);

        Object token = login.getBody().get("accessToken");
        assertThat(token).isInstanceOf(String.class);
        assertThat((String) token).isNotBlank();
        return (String) token;
    }
}
