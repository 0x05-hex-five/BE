package hexfive.ismedi.fastApi;

import hexfive.ismedi.fastApi.dto.AiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FastApiClient {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public List<AiResponseDto> sendImage(Path imagePath){
        FileSystemResource imageResource = new FileSystemResource(imagePath);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", imageResource); // FastAPI에서 image 파라미터로 받아야 함

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate template = new RestTemplate();
        ResponseEntity<List<AiResponseDto>> response = template.exchange(
                aiServerUrl + "/predict",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<AiResponseDto>>() {}
        );
        return response.getBody();
    }

}
