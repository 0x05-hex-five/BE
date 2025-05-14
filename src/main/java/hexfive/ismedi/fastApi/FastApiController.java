package hexfive.ismedi.fastApi;

import hexfive.ismedi.global.response.APIResponse;
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
public class FastApiController {
    private final FastApiService fastApiService;

    @PostMapping(value = "/recognitions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> recognition(@RequestParam("image") MultipartFile imageFile) {
        String path = fastApiService.saveImage(imageFile);
        return APIResponse.success(path);
    }
}
