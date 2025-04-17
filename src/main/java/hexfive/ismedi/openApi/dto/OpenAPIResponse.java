package hexfive.ismedi.openApi.dto;

// LocalDataResponse.java
import lombok.Getter;

@Getter
public class OpenAPIResponse<T> {
    private OpenAPIHeader header;
    private OpenAPIBody<T> body;
}