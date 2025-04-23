package hexfive.ismedi.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTokenResponseDto {
    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("expires_in")
    private int expires_in;

    @JsonProperty("scope")
    private String scope;
}
