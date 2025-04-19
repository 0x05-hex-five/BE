package hexfive.ismedi.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import java.util.Map;

@Getter
public enum ErrorCode {
    // 인증/인가 관련
    TOKEN_EXPIRED("TOKEN_EXPIRED", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    MISSING_TOKEN("MISSING_TOKEN", "Access Token이 없습니다", HttpStatus.UNAUTHORIZED),
    LOGOUT_FAILED("LOGOUT_FAILED", "로그아웃 처리 중 문제가 발생했습니다. 토큰이 이미 만료되었거나 존재하지 않습니다.", HttpStatus.BAD_REQUEST),


    // 요청 오류 관련
    VALIDATION_FAILED("VALIDATION_FAILED", "요청값이 유효하지 않습니다", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "이미 등록된 이메일입니다", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_METHOD("UNSUPPORTED_METHOD", "지원하지 않는 HTTP 메서드입니다", HttpStatus.METHOD_NOT_ALLOWED),

    // AI 관련
    AI_SERVER_ERROR("AI_SERVER_ERROR", "AI 서버와 통신할 수 없습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 데이터 관련
    DATA_NOT_FOUND("DATA_NOT_FOUND", "데이터를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // 기타
    INTERNAL_ERROR("INTERNAL_ERROR", "서버 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public int getStatusCode() {
        return status.value();
    }
}
