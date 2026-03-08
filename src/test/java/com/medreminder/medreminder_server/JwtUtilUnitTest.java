package com.medreminder.medreminder_server;

import com.medreminder.medreminder_server.application.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilUnitTest {

    @Mock
    private Environment environment;

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = Encoders.BASE64.encode(
        Jwts.SIG.HS256.key().build().getEncoded()
    );

    @BeforeEach
    void setUp() {
        when(environment.getProperty(any(String.class))).thenReturn(TEST_SECRET);
        jwtUtil = new JwtUtil(environment);
    }

    @Test
    void generateToken_ShouldReturnNonNullToken(){

        String token = jwtUtil.generateToken("testUser@mail.com");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void generateToken_ShouldContainCorrectSubject() {
        String email = "testUser@mail.com";
        String token = jwtUtil.generateToken(email);

        String subject = jwtUtil.extractEmail(token);

        assertThat(subject).isEqualTo(email);
    }

    @Test
    void generateToken_ShouldHaveAccessTokenType() {
        String email = "testUser@mail.com";

        String token = jwtUtil.generateToken(email);

        String claimType = jwtUtil.extractClaim(token, "token_type");

        assertThat(claimType).isEqualTo("access");
    }
}
