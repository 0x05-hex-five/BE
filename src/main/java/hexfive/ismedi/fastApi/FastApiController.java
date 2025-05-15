package hexfive.ismedi.fastApi;

import hexfive.ismedi.fastApi.dto.AiResponseDto;
import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.global.swagger.FastApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class FastApiController implements FastApiDocs {
    private final FastApiService fastApiService;

    @PostMapping(value = "/recognitions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<AiResponseDto> recognition(@RequestParam("image") MultipartFile imageFile) {
        return APIResponse.success(fastApiService.recognize(imageFile));
    }
}
