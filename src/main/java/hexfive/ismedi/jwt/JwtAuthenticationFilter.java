package hexfive.ismedi.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // doFilter() : GenericFilterBean을 상속받아 필터로 작동
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getRequestURI().equals("/api/auth/reissue") || httpRequest.getRequestURI().equals("/api/auth/logout")) {
            chain.doFilter(request, response);
            return;
        }

        String token = resolveToken(httpRequest);
        if (token != null && jwtProvider.isBlacklisted(token)) {
            ErrorCode errorCode = ErrorCode.LOGOUT_TOKEN;

            Map<String, Object> error = new HashMap<>();
            error.put("code", errorCode.getCode());
            error.put("status", errorCode.getStatusCode());

            httpResponse.setStatus(errorCode.getStatusCode());
            httpResponse.setContentType("application/json;charset=UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(
                    APIResponse.fail(errorCode.getMessage())
            );

            httpResponse.getWriter().write(json);
            return;
        }

        if (token != null) { // 존재하면 유효성 검사
            try{
                if (jwtProvider.validateAccessToken(token)) {
                    // 유효하다면 Authentication 객체 생성
                    Authentication authentication = jwtProvider.getAuthentication(token);
                    // SecurityContextHolder에 인증 객체 설정 -> 컨트롤러, 서비스 계층에서 @AuthenticationPrincipal로 접근 가능
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e){
                // 토큰 만료
                ErrorCode errorCode = ErrorCode.TOKEN_EXPIRED;

                Map<String, Object> errorObject = new HashMap<>();
                errorObject.put("code", errorCode.getCode());
                errorObject.put("status", errorCode.getStatusCode());
                //errorObject.put("message", errorCode.getMessage());
                //errorObject.put("details", Map.of("tokenExpired", true));

                httpResponse.setStatus(errorCode.getStatusCode());
                httpResponse.setContentType("application/json;charset=UTF-8");

                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(
                        APIResponse.fail(errorCode.getMessage())
                );

                httpResponse.getWriter().write(json);
                return;
            } catch (JwtException | IllegalArgumentException e) {
                // 비정상 토큰
                ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

                Map<String, Object> error = new HashMap<>();
                error.put("code", errorCode.getCode());
                error.put("status", errorCode.getStatusCode());
                //error.put("message", errorCode.getMessage());
                //error.put("details", Map.of("invalidToken", true));

                httpResponse.setStatus(errorCode.getStatusCode());
                httpResponse.setContentType("application/json;charset=UTF-8");

                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(
                        APIResponse.fail(errorCode.getMessage())
                );

                httpResponse.getWriter().write(json);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
