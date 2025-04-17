package hexfive.ismedi.medicine;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}