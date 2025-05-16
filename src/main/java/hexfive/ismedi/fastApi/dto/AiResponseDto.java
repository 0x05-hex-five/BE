package hexfive.ismedi.fastApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {
    private Long id;
    private double confidence;
}
