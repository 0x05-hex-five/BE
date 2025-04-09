package hexfive.ismedi.openApi;

import hexfive.ismedi.openApi.dto.DrugInfoDto;
import hexfive.ismedi.openApi.dto.PrescriptionTypeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ApiType {
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

    public static ApiType from(String type) {
        return Arrays.stream(values())
                .filter(apiType -> apiType.typeName.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 API type입니다: " + type));
    }
}
