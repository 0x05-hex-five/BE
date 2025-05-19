package hexfive.ismedi.global.exception;

import hexfive.ismedi.global.response.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class IsMediExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<APIResponse<Void>> handleGeneralException(CustomException ex) {
        log.error("Exception occurred: {}, Type: {}", ex.getMessage(), ex.getClass().getSimpleName());

        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(APIResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Exception occurred: {}, Type: {}", ex.getMessage(), ex.getClass().getSimpleName());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("입력값이 올바르지 않습니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.fail(errorMessage));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<APIResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ResponseEntity
                .status(ErrorCode.MAX_FILE_SIZE_EXCEEDED.getStatus())
                .body(APIResponse.fail(ErrorCode.MAX_FILE_SIZE_EXCEEDED.getMessage()));
    }
}