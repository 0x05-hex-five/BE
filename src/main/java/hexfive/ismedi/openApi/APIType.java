package hexfive.ismedi.openApi;

import hexfive.ismedi.medicine.dto.DURInteractionDto;
import hexfive.ismedi.openApi.data.drugInfo.DrugInfo;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionType;
import hexfive.ismedi.openApi.data.xml.DrugItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum APIType {
    DRUG_INFO(
            "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList",
            "drug-info",
            DrugInfo.class
    ),
    PRESCRIPTION_TYPE(
            "http://apis.data.go.kr/1471000/DrugPrdlstVldPrdInfoService01/getDrugPrdlstVldPrdInfoService01",
            "prescription-type",
            PrescriptionType.class
    ),
    DUR_INTERACTION(
            "http://apis.data.go.kr/1471000/DURPrdlstInfoService03/getUsjntTabooInfoList03",
            "dur-interaction",
            DURInteractionDto.class
    ),

    XML(
            "https://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService06/getDrugPrdtPrmsnDtlInq05",
            "xml",
            DrugItem.class
    );
    private final String url;
    private final String typeName;
    private final Class<?> entity;

    public static APIType from(String type) {
        return Arrays.stream(values())
                .filter(apiType -> apiType.typeName.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 API type입니다: " + type));
    }
}
