package hexfive.ismedi.openApi.dto;

// LocalDataResponse.java
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LocalDataResponse<T> {
    private DrugInfoHeader header;
    private DrugInfoBody<T> body;
}