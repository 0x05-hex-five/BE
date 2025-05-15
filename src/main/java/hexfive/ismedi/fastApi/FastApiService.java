package hexfive.ismedi.fastApi;

import hexfive.ismedi.fastApi.dto.AiResponseDto;
import hexfive.ismedi.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static hexfive.ismedi.global.exception.ErrorCode.INTERNAL_ERROR;

@Service
@RequiredArgsConstructor
public class FastApiService {
    public final FastApiClient fastApiClient;
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/"; // 톰캣 내장서버 기준 말고 현재 서버 기준으로

    public AiResponseDto recognize(MultipartFile imageFile){
        String path = saveImage(imageFile);
        Path imagePath = Paths.get(path);
        try{
            AiResponseDto response = sendToAiServer(imagePath);
            deleteImage(imagePath);
            return response;
        } catch (Exception e){
            try{deleteImage(imagePath);} catch(Exception ignore){}
            throw new CustomException(INTERNAL_ERROR);
        }
    }

    public String saveImage(MultipartFile imageFile) {
        try{
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String imageName = imageFile.getOriginalFilename();
            String ext = "";
            if(imageName != null && imageName.contains(".")){
                ext = imageName.substring(imageName.lastIndexOf("."));
            }

            String savedFileName = UUID.randomUUID() + ext;
            Path filePath = uploadDir.resolve(savedFileName);

            imageFile.transferTo(filePath.toFile());

            return filePath.toString();
        } catch (IOException e) {
            throw new CustomException(INTERNAL_ERROR);
        }
    }

    public AiResponseDto sendToAiServer(Path imagePath) {
        return fastApiClient.sendImage(imagePath);
    }

    private void deleteImage(Path imagePath) throws IOException {
        Files.deleteIfExists(imagePath);
    }
}
