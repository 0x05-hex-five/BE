package hexfive.ismedi.fastApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {
    private List<Integer> box;
    private String top_class;
    private double top_prob;
    private List<AiClassProbDto> all_classes;
}
