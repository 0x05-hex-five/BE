package hexfive.ismedi.fastApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseWrapperDto {
    private boolean status;
    private boolean detected;
    private AiResponseDto data;  // null일 수 있음
}
