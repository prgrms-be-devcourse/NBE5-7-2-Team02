package io.twogether.nbe_5_7_2_02team.oauth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import io.twogether.nbe_5_7_2_02team.oauth.dao.TokenRepository;
import io.twogether.nbe_5_7_2_02team.oauth.dto.TokenBody;
import io.twogether.nbe_5_7_2_02team.oauth.dto.TokenPair;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfiguration jwtConfiguration;
    private final TokenRepository tokenRepository;

    public TokenPair generateTokenPair(Member member) {

        String accessToken = issueAccessToken(member.getId(), member.getRole());
        String refreshToken = issueRefreshToken(member.getId(), member.getRole());

        tokenRepository.save(member, refreshToken);

        return TokenPair.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public String issueAccessToken(Long id, Role role) {
        return issue(id, role, jwtConfiguration.getValidation().getAccess());
    }

    public String issueRefreshToken(Long id, Role role) {
        return issue(id, role, jwtConfiguration.getValidation().getRefresh());
    }

    private String issue(Long id, Role role, Long expTime) {
        return Jwts.builder()
            .subject(id.toString())
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(new Date().getTime() + expTime))
            .signWith(getSecretKey(), SIG.HS256)
            .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public TokenBody parseJwt(String token) {
        Jws<Claims> parsed = Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token);

        String sub = parsed.getPayload().getSubject();
        String role = parsed.getPayload().get("role").toString();

        return new TokenBody(
            Long.parseLong(sub)
            , Role.valueOf(role)
        );
    }


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtConfiguration.getSecrets().getAppKey().getBytes());
    }

}
