package hexfive.ismedi.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import hexfive.ismedi.jwt.TokenDto;
import hexfive.ismedi.oauth.dto.KakaoUserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KaKaoLoginResponseDto {
    private boolean isNew;          // 신규 회원 여부
    private TokenDto token;         // 기존 회원이면 토큰 포함
    @JsonProperty("user")
    private KakaoUserInfoDto userInfo;     // 사용자 정보
}
