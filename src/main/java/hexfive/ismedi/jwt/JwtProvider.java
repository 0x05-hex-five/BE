package hexfive.ismedi.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
//import org.springframework.data.redis.core.StringRedisTemplate;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKeyString;

    //private final StringRedisTemplate redisTemplate;

    private Key secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60;               // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 14;    // 2주

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access 토큰 생성하는 메서드
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh 토큰을 생성하는 메서드
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME); // 7일

        // Redis에 저장 (key: RT:<email>, value: refreshToken)

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Access 토큰의 유효성 검증하는 메서드
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token); // 여기서 만료 및 형식 검증 수행
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
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Refresh Token 만료: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.info("유효하지 않은 Refresh Token: {}", e.getMessage());
        }
        return false;
    }



    // Refresh 토큰 재발급하는 메서드 (미완)
    public String reissueAccessToken(String refreshToken) {
        // Redis 공부 해보고 구현하는걸로
        return "";
    }

    // 로그아웃 시 redis refresh token 삭제하는 메서드 (미완)




    // 토큰에서 subject 추출 후 인증 객체 생성하는 메서드
    public Authentication getAuthentication(String token) {
        // JwtAuthenticationFilter.doFilter() 에서 토큰이 유효할 경우 호출됨
        // "이 사용자는 인증되었다" 판단할 수 있도록 Authentication 객체를 만들어서 Spring Security에 넘겨줌

        if (!validateAccessToken(token)) {
            log.info("JWT 토큰 유효성 검증 실패");
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        }

        Claims claims = getClaimsFromToken(token);
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
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.info("토큰에서 payload 추출 실패: " + e.getMessage());
            throw new RuntimeException("토큰에서 payload 추출 실패: " + e.getMessage());
        }
    }
}
