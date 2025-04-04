package hexfive.ismedi.global;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class APIResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;

    public static <T> APIResponse<T> success(T data) {
        return APIResponse.<T>builder()
            .success(true)
            .message("성공적으로 응답되었습니다")
            .data(data)
            .build();
    }

    public static <T> APIResponse<T> fail(String message) {
        return APIResponse.<T>builder()
            .success(false)
            .message(message)
            .data(null)
            .build();
    }
}