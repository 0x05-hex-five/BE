package hexfive.ismedi.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import java.util.Map;

@Getter
public enum ErrorCode {
    // 인증/인가 관련
    TOKEN_EXPIRED("TOKEN_EXPIRED", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    LOGOUT_TOKEN("LOGOUT_TOKEN", "로그아웃된 토큰입니다", HttpStatus.UNAUTHORIZED),
    MISSING_TOKEN("MISSING_TOKEN", "Access Token이 없습니다", HttpStatus.UNAUTHORIZED),
    LOGOUT_FAILED("LOGOUT_FAILED", "로그아웃 처리 중 문제가 발생했습니다. 토큰이 이미 만료되었거나 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED("LOGIN_FAILED", "로그인 정보(이름, 이메일)가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "해당 데이터에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN),

    // Redis 관련
    REDIS_ERROR("REDIS_ERROR", "토큰 저장소(Redis) 처리 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 요청 오류 관련
    VALIDATION_FAILED("VALIDATION_FAILED", "요청값이 유효하지 않습니다", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "이미 등록된 이메일입니다", HttpStatus.BAD_REQUEST),

    // AI 관련
    AI_SERVER_ERROR("AI_SERVER_ERROR", "AI 서버와 통신할 수 없습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 데이터 관련
    DATA_NOT_FOUND("DATA_NOT_FOUND", "데이터를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // OpenAPI 호출 관련
    INVALID_API_TYPE("INVALID_API_TYPE", "지원하지 않는 API 타입입니다: %s", HttpStatus.BAD_REQUEST),
    MISMATCH_COUNT("MISMATCH_COUNT", "처리된 건수 불일치: 저장 %d + 스킵 %d ≠ 전체 %d", HttpStatus.BAD_REQUEST),

    // User
    USER_NOT_FOUND("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ACCESS_DENIED("ACCESS_DENIED", "해당 리소스에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // Category
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "카테고리(id=%d)를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // Notification
    NOTIFICATION_NOT_FOUND("CATEGORY_NOT_FOUND", "알림(id=%d)를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

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
