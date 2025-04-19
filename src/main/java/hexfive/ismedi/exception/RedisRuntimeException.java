package hexfive.ismedi.exception;

public class RedisRuntimeException extends RuntimeException {
    public RedisRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

