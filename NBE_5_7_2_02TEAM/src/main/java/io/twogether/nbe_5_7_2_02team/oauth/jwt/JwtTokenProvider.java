package io.twogether.nbe_5_7_2_02team.oauth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import io.twogether.nbe_5_7_2_02team.oauth.dao.RefreshTokenBlackListRepository;
import io.twogether.nbe_5_7_2_02team.oauth.dao.RefreshTokenRepository;
import io.twogether.nbe_5_7_2_02team.oauth.dao.TokenRepository;
import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshToken;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenBody;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfiguration jwtConfiguration;
    private final TokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenBlackListRepository refreshTokenBlackListRepository;

    public TokenPair generateTokenPair(Member member) {

        refreshTokenRepository
                .findByMemberId(member.getId())
                .ifPresent(
                        refreshToken -> {
                            refreshTokenBlackListRepository.deleteByRefreshToken(refreshToken);
                            refreshTokenRepository.delete(refreshToken);
                        });

        String accessToken = issueAccessToken(member.getId(), member.getRole());
        String refreshToken = issueRefreshToken(member.getId(), member.getRole());

        tokenRepository.save(member, refreshToken);

        return TokenPair.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public Optional<RefreshToken> findRefreshToken(Long memberId) {
        return tokenRepository.findValidRefToken(memberId);
    }

    public void addBlackList(RefreshToken refreshToken) {
        tokenRepository.addBlackList(refreshToken);
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
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new ErrorException(ErrorCode.INVALID_ACCESS_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new ErrorException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new ErrorException(ErrorCode.UNSUPPORTED_ACCESS_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new ErrorException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    public void refreshValidate(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);

        } catch (SecurityException | MalformedJwtException e) {
            throw new ErrorException(ErrorCode.INVALID_REFRESH_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new ErrorException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new ErrorException(ErrorCode.UNSUPPORTED_REFRESH_TOKEN);
        }
    }

    public TokenBody parseJwt(String token) {
        Jws<Claims> parsed =
                Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);

        String sub = parsed.getPayload().getSubject();
        String role = parsed.getPayload().get("role").toString();

        return TokenBody.builder().memberId(Long.parseLong(sub)).role(Role.valueOf(role)).build();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtConfiguration.getSecrets().getAppKey().getBytes());
    }
}
