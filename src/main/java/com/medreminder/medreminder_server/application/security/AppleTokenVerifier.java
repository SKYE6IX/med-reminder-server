package com.medreminder.medreminder_server.application.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Service
public class AppleTokenVerifier {
    private final Environment env;

    @Autowired
    public AppleTokenVerifier(Environment env) {
        this.env = env;
    }

    public boolean verifyToken(String token) {

        String clientId = env.getProperty("med.reminder.apple.client.id");

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
                    .withAudience(clientId)
                    .build()
                    .verify(token);

            return verified.getAudience().contains(clientId);

        }catch (Exception e){
            throw new BadCredentialsException("Apple token verification failed!");
        }
    }
}
