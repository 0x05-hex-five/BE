package hexfive.ismedi.fastApi;

import hexfive.ismedi.fastApi.dto.AiResponseDto;
import hexfive.ismedi.fastApi.dto.AiResponseWrapperDto;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.global.response.APIResponse;
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

import static hexfive.ismedi.global.exception.ErrorCode.AI_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class FastApiClient {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public AiResponseDto sendImage(Path imagePath){
        FileSystemResource imageResource = new FileSystemResource(imagePath);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", imageResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<AiResponseWrapperDto> response = template.postForEntity(
                    aiServerUrl + "/ai/predict",
                    requestEntity,
                    AiResponseWrapperDto.class
            );

            AiResponseWrapperDto responseBody = response.getBody();
            if(responseBody == null || !responseBody.isSuccess()){
                throw new CustomException(AI_SERVER_ERROR);
            }

            return responseBody.getData();
        }catch (Exception e){
            throw new CustomException(AI_SERVER_ERROR);
        }
    }
}
