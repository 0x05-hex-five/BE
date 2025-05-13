package hexfive.ismedi.medicine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResInteractionDto {
    private String itemName1;
    private String itemName2;

    private boolean isProhibit; // 금기여부
    private String prohibitContent; // 금기내용
}
