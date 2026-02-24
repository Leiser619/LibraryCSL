package pl.csl.library.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import pl.csl.library.service.JwtService;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void should_generate_and_validate_token_and_extract_claims() {
        String secret = "KLUCZTAJNYMEGATAJNYSUPERMEGATAJNYJAKNIEWIEM123456789";
        long expirationMinutes = 60;

        JwtService jwtService = new JwtService(secret, expirationMinutes);

        String token = jwtService.generateToken(123L, "test@test.com");
        assertThat(token).isNotBlank();

        var jws = jwtService.parseAndValidate(token);
        Claims claims = jws.getBody();

        assertThat(jwtService.extractEmail(claims)).isEqualTo("test@test.com");
        assertThat(jwtService.extractUserId(claims)).isEqualTo(123L);
    }
}