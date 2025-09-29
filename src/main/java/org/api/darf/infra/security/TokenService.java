package org.api.darf.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.api.darf.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Lê a secret de variável de ambiente ou do application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera um token JWT assinado com HMAC256.
     * @param user Usuário autenticado
     * @return Token JWT gerado
     */
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("login-auth-api")             // Quem emitiu o token
                    .withSubject(user.getCpf())               // Identificador do usuário
                    .withIssuedAt(Instant.now())               // Data de emissão
                    .withExpiresAt(generateExpirationDate())  // Data de expiração
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Valida o token JWT e retorna o CPF do usuário.
     * @param token Token JWT
     * @return CPF do usuário, ou null se inválido
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return null; // Token inválido ou expirado
        }
    }

    /**
     * Define a data de expiração do token para 2 horas no futuro.
     */
    private Instant generateExpirationDate() {
        return LocalDateTime.now()
                .plusHours(8)
                .toInstant(ZoneOffset.of("-04:00")); // Fuso horário de Brasília
    }
}
