package hexfive.ismedi.fastApi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FastApiService {
    public final String UPLOAD_DIR = "uploads/";

    public String saveImage(MultipartFile imageFile) {
        try{
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectory(uploadDir);
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
            throw new RuntimeException("이미지 저장 중 오류 발생", e);
        }
    }
}
