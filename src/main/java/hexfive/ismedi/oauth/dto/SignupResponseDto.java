package hexfive.ismedi.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@Schema(description = "회원가입 응답 DTO")
public class SignupResponseDto {

    @Schema(description = "회원 고유 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "사용자 이메일", example = "hong@example.com")
    private String email;
}
