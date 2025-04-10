package hexfive.ismedi.openApi.data.drugInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrugInfoDto {
    private String entpName;       // 업체명
    private String itemName;       // 제품명
    private String itemSeq;        // 품목기준코드
    private String efcyQesitm;     // 효능
    private String useMethodQesitm;// 사용법
    private String atpnQesitm;     // 주의사항
    private String intrcQesitm;    // 상호작용
    private String seQesitm;       // 부작용
    private String depositMethodQesitm; // 보관법

    public DrugInfo toEntity() {
        return DrugInfo.builder()
                .entpName(entpName)
                .itemName(itemName)
                .itemSeq(itemSeq)
                .efcyQesitm(efcyQesitm)
                .useMethodQesitm(useMethodQesitm)
                .atpnQesitm(atpnQesitm)
                .intrcQesitm(intrcQesitm)
                .seQesitm(seQesitm)
                .depositMethodQesitm(depositMethodQesitm)
                .build();
    }
}