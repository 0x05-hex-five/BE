package hexfive.ismedi.fastApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiClassProbDto {
    @JsonProperty("class_id")
    private Long classId;

    private double confidence;
}
