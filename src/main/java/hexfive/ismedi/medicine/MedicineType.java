package hexfive.ismedi.medicine;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MedicineType {
    ALL("전체"),
    ETC("전문의약품"),
    OTC("일반의약품");

    private final String value;

    public boolean isAll() {
        return this == ALL;
    }

    public static MedicineType from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("의약품 분류는 ALL, ETC(전문), OTC(일반) 중 하나 이어야 합니다. 올바르지 않은 값 : " + value));
    }
}