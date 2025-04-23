package hexfive.ismedi.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
//import org.springframework.data.redis.core.StringRedisTemplate;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.access-secret}")
    private String accessSecretKeyString;
    @Value("${jwt.refresh-secret}")
    private String refreshSecretKeyString;
    @Getter
    private Key accessSecretKey;
    @Getter
    private Key refreshSecretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60;               // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 14;    // 2주
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        this.accessSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretKeyString));
        this.refreshSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretKeyString));
    }

    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(accessSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME); // 7일

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(refreshSecretKey, SignatureAlgorithm.HS256)
                .compact();

        redisTemplate.opsForValue().set("refresh:" + userId, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessSecretKey)
                    .build()
                    .parseClaimsJws(token) // 여기서 만료 및 형식 검증 수행
                    .getBody();

            String type = claims.get("type", String.class);
            return "access".equals(type);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Invalid JWT token", e);
        }
    }

    // Refresh 토큰 유효성 검증하는 메서드
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshSecretKey)
                    .build()
                    .parseClaimsJws(token) // 여기서 만료 및 형식 검증 수행
                    .getBody();

            String type = claims.get("type", String.class);
            return "refresh".equals(type);
        } catch (ExpiredJwtException e) {
            log.info("Refesh Token 만료: {}", e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("유효하지 않은 Refesh Token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token", e);
        }
    }

    // Refresh 토큰 재발급하는 메서드
    public TokenDto reissueAccessToken(String refreshToken) {

        Claims claims = getClaimsFromToken(refreshToken, TokenType.REFRESH);
        String userId = claims.getSubject();

        String storedToken = redisTemplate.opsForValue().get("refresh:" + userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new JwtException("저장된 Refresh Token과 일치하지 않습니다.");
        }

        String newAccessToken = generateAccessToken(Long.valueOf(userId));
        String newRefreshToken = generateRefreshToken(Long.valueOf(userId));

        return TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(Long.valueOf(userId))
                .build();
    }

    // 로그아웃 시 redis refresh token 삭제하는 메서드
    public Boolean deleteRefreshToken(String AccessToken) {
        Boolean result = redisTemplate.delete(
                "refresh:" + getClaimsFromToken(AccessToken, TokenType.ACCESS).getSubject()
        );
        return Boolean.TRUE.equals(result);
    }

    // 삭제된 refresh 토큰 블랙리스트 Redis 추가하는 메서드
    public void addToBlacklist(String token) {
        redisTemplate.opsForValue()
                .set("blacklist:" + token, "logout", getExpiration(token), TimeUnit.MILLISECONDS);
    }

    // 필터에서 토큰 블랙리스트 확인
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }

    // 토큰의 남은 만료 시간 구하기
    public long getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }


    // 토큰에서 subject 추출 후 인증 객체 생성하는 메서드
    public Authentication getAuthentication(String token) {
        // UserDetails 객체 생성 : Spring Security에서 사용자의 정보를 담는 인터페이스 객체
        UserDetails userDetails = new User(
                getClaimsFromToken(token, TokenType.ACCESS).getSubject(),
                "",
                Collections.emptyList()
        );
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    // 토큰에서 claim 추출하는 메서드
    public Claims getClaimsFromToken(String token, TokenType type) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(type.resolveKey(this))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("토큰에서 payload 추출 실패: " + e.getMessage());
        }
    }
}
