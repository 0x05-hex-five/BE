package hexfive.ismedi.fastApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AiClassProbDto {
    @JsonProperty("class_id")
    private String classId;

    private double confidence;
}
