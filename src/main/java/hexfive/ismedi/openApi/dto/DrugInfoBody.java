package hexfive.ismedi.openApi.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DrugInfoBody {
    private int pageNo;
    private int totalCount;
    private int numOfRows;
    private List<DrugInfoDto> items;
}
