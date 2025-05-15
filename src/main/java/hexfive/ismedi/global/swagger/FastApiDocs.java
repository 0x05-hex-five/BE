package hexfive.ismedi.global.swagger;

import hexfive.ismedi.fastApi.dto.AiResponseDto;
import hexfive.ismedi.global.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "FastAPI", description = "AI 약 이미지 분석 API")
public interface FastApiDocs {

    @Operation(
            summary = "약 이미지 인식",
            description = "이미지 파일을 업로드하면 AI 모델을 통해 약 이름 및 추론 결과를 반환합니다."
    )
    @PostMapping(value = "/api/ai/recognitions", consumes = "multipart/form-data")
    APIResponse<AiResponseDto> recognition(
            @RequestParam("image") MultipartFile imageFile
    );
}
