package hexfive.ismedi.openApi.dto;

// LocalDataResponse.java
import lombok.Getter;

@Getter
public class OpenApiResponse<T> {
    private OpenApiHeader header;
    private OpenApiBody<T> body;
}