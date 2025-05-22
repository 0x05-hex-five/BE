package hexfive.ismedi.global.config;

import hexfive.ismedi.jwt.JwtAuthenticationFilter;
import hexfive.ismedi.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
//            .cors(cors -> cors.configurationSource(request -> {
//                CorsConfiguration config = new CorsConfiguration();
//                config.addAllowedOrigin("http://localhost:3000");     // 개발용
//                //config.addAllowedOrigin("domain");                  // 배포용
//                config.addAllowedMethod("OPTIONS", "GET", "POST", "PUT", "DELETE");
//                config.addAllowedHeader("*");
//                config.setAllowCredentials(true);                     // 프론트에서 쿠키 사용할 경우 열어둠
//                config.setMaxAge(3600L);                              // preflight (OPTIONS 응답 결과) 캐싱 시간
//                return config;
//            }))
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers( // 인증 없이 허용할 URI
                        "/api/auth/**",
                        "/api/interactions/**",
                        "/api/medicines/**",
                        "/api/fetch/**",
                        "/api/ai/recognitions",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
