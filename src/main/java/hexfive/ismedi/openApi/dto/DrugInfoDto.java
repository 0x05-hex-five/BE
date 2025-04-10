package hexfive.ismedi.openApi.dto;

import hexfive.ismedi.domain.Medicine;
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
    private String itemImage;      // 낱알이미지

    public Medicine toMedicine() {
        return Medicine.builder()
                .entpName(entpName)
                .itemName(itemName)
                .itemSeq(itemSeq)
                .efcyQesitm(efcyQesitm)
                .useMethodQesitm(useMethodQesitm)
                .atpnQesitm(atpnQesitm)
                .intrcQesitm(intrcQesitm)
                .seQesitm(seQesitm)
                .depositMethodQesitm(depositMethodQesitm)
                .itemImage(itemImage)
                .build();
    }
}