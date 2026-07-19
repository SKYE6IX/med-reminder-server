package com.medreminder.medreminder_server.application.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.medreminder.medreminder_server.application.dtos.AppleTokenResponse;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class AppleAuth {


    private final RestClient restClient;
    private final Environment env;

    @Autowired
    public AppleAuth(RestClient restClient, Environment env) {
        this.restClient = restClient;
        this.env = env;
    }

    public boolean verifyToken(String token) {
        String bundleId = env.getProperty("apple.bundle.id");
        try {
            URL url = URI.create("https://appleid.apple.com/auth/keys").toURL();
            JwkProvider provider = new UrlJwkProvider(
              url
            );

            DecodedJWT decoded = JWT.decode(token);
            Jwk jwk = provider.get(decoded.getKeyId());

            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) jwk.getPublicKey(), null
            );
            DecodedJWT verified = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .withAudience(bundleId)
                    .build()
                    .verify(token);
            return verified.getAudience().contains(bundleId);

        }catch (Exception e){
            throw new BadCredentialsException("Apple token verification failed!");
        }
    }

    public AppleTokenResponse exchangeCodeForRefreshToken(String authorizationCode) throws Exception {
        final String bundleId = env.getProperty("apple.bundle.id");
        String clientSecret = generateClientSecret();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", bundleId);
        body.add("client_secret", clientSecret);
        body.add("code", authorizationCode);
        body.add("grant_type", "authorization_code");

        return restClient.post()
                .uri("/auth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(AppleTokenResponse.class);
    }

    public void revokeAppleUserToken(String token) throws Exception {
        final String bundleId = env.getProperty("apple.bundle.id");
        String clientSecret = generateClientSecret();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("client_id", bundleId);
        body.add("client_secret", clientSecret);
        body.add("token", token);
        body.add("token_type_hint","refresh_token");

        restClient.post()
                .uri("/auth/revoke")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }


    private String generateClientSecret() throws Exception {
        final String teamId = env.getProperty("apple.team.id");
        final String keyId = env.getProperty("apple.key.id");
        final String bundleId = env.getProperty("apple.bundle.id");
        final String privateKeyPath = env.getProperty("apple.private.key.path");

        PrivateKey privateKey = loadPrivateKey(privateKeyPath);

        return Jwts.builder()
                .header()
                .keyId(keyId)
                .and()
                .issuer(teamId)
                .subject(bundleId)
                .audience().add("https://appleid.apple.com").and()
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(path)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }
}
