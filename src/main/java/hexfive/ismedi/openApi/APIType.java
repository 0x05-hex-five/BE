package hexfive.ismedi.openApi;

import hexfive.ismedi.openApi.data.drugInfo.DrugInfoDto;
import hexfive.ismedi.openApi.data.prescriptionType.PrescriptionTypeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum APIType {
    DRUG_INFO(
            "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList",
            "drug-info",
            DrugInfoDto.class
    ),
    PRESCRIPTION_TYPE(
            "http://apis.data.go.kr/1471000/DrugPrdlstVldPrdInfoService01/getDrugPrdlstVldPrdInfoService01",
            "prescription-type",
            PrescriptionTypeDto.class
    );
    private final String url;
    private final String typeName;
    private final Class<?> dtoClass;

    public static APIType from(String type) {
        return Arrays.stream(values())
                .filter(apiType -> apiType.typeName.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 API type입니다: " + type));
    }
}
