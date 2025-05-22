package hexfive.ismedi.fastApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {
    private List<Integer> box;

    @JsonProperty("primary_prediction")
    private AiClassProbDto primaryPrediction;

    @JsonProperty("top_predictions")
    private List<AiClassProbDto> topPredictions;
}
