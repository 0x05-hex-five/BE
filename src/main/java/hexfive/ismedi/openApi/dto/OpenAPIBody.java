package hexfive.ismedi.openApi.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class OpenAPIBody<T> {
    private int pageNo;
    private int totalCount;
    private int numOfRows;
    private List<T> items;
    public List<T> getItems() {
        return items != null ? items : Collections.emptyList();
    }
}
