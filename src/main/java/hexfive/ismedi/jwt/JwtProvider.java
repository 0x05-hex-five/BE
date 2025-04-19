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
    //private final StringRedisTemplate redisTemplate;

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


    // Access 토큰 생성하는 메서드
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(accessSecretKey, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    // Refresh 토큰을 생성하는 메서드
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

        // Redis에 저장 (key: RT:<email>, value: refreshToken)
        String key = "refresh:" + userId;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    // Access 토큰의 유효성 검증하는 메서드
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessSecretKey)
                    .build()
                    .parseClaimsJws(token) // 여기서 만료 및 형식 검증 수행
                    .getBody();

            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                log.warn("Access Token이 아님!");
                return false;
            }
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Access Token 만료: {}", e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("유효하지 않은 Access Token: {}", e.getMessage());
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
            if (!"refresh".equals(type)) {
                log.warn("Refesh Token이 아님!");
                return false;
            }
            return true;
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
        // 1. 유효성 검사
        if (!validateRefreshToken(refreshToken)) {
            throw new JwtException("유효하지 않은 Refresh Token입니다.");
        }

        // 2. claims에서 userId 추출
        Claims claims = getClaimsFromToken(refreshToken, TokenType.REFRESH);
        String userId = claims.getSubject();
        String redisKey = "refresh:" + userId;

        // 3. Redis에 저장된 refresh 토큰과 비교
        String storedToken = redisTemplate.opsForValue().get(redisKey);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new JwtException("저장된 Refresh Token과 일치하지 않습니다.");
        }

        // 4. Access Token 재발급
        String newAccessToken = generateAccessToken(Long.valueOf(userId));
        String newRefreshToken = generateRefreshToken(Long.valueOf(userId));

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(Long.valueOf(userId))
                .build();

        return tokenDto;
    }

    // 로그아웃 시 redis refresh token 삭제하는 메서드
    public void deleteRefreshToken(String accessToken) {
        // Access Token이 유효해야 사용자 식별 가능
        if (!validateAccessToken(accessToken)) {
            throw new JwtException("유효하지 않은 Access Token입니다.");
        }

        Claims claims = getClaimsFromToken(accessToken, TokenType.ACCESS);
        String userId = claims.getSubject();

        String key = "refresh:" + userId;
        Boolean result = redisTemplate.delete(key);

        if (Boolean.TRUE.equals(result)) {
            log.info("Redis에서 Refresh Token 삭제 성공: {}", userId);
        } else {
            log.warn("Redis에서 Refresh Token 삭제 실패 or 존재하지 않음: {}", userId);
        }
    }

    // 삭제된 refresh 토큰 블랙리스트 Redis 추가하는 메서드


    // 토큰에서 subject 추출 후 인증 객체 생성하는 메서드
    public Authentication getAuthentication(String token) {
        // JwtAuthenticationFilter.doFilter() 에서 토큰이 유효할 경우 호출됨
        // "이 사용자는 인증되었다" 판단할 수 있도록 Authentication 객체를 만들어서 Spring Security에 넘겨줌

        if (!validateAccessToken(token)) {
            log.info("JWT 토큰 유효성 검증 실패");
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        }

        Claims claims = getClaimsFromToken(token, TokenType.ACCESS);
        if (claims == null) {
            log.info("JWT 클레임이 null입니다.");
            throw new RuntimeException("JWT 클레임을 추출할 수 없습니다.");
        }

        String subject = claims.getSubject();

        // UserDetails 객체 생성 : Spring Security에서 사용자의 정보를 담는 인터페이스 객체
        UserDetails userDetails = new User(
                subject,
                "",
                Collections.emptyList()
        );

        // UsernamePasswordAuthenticationToken : Authentication 인터페이스를 구현한 클래스
        // Spring Security는 이걸 사용해서 "인증 완료된 사용자 정보" 를 담아 보관
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    // 토큰에서 claim(payload) 추출하는 메서드
    public Claims getClaimsFromToken(String token, TokenType type) {
        if (type == null) {
            throw new IllegalArgumentException("TokenType은 null일 수 없습니다.");
        }
        Key key = type.resolveKey(this);

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.info("토큰에서 payload 추출 실패: " + e.getMessage());
            throw new RuntimeException("토큰에서 payload 추출 실패: " + e.getMessage());
        }
    }
}
