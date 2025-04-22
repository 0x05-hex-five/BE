package hexfive.ismedi.jwt;

import java.security.Key;

public enum TokenType {
    ACCESS,
    REFRESH;

    public Key resolveKey(JwtProvider provider) {
        return switch (this) {
            case ACCESS -> provider.getAccessSecretKey();
            case REFRESH -> provider.getRefreshSecretKey();
        };
    }
}
