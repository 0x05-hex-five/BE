package hexfive.ismedi.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class NotificationDto {

    @NotNull(message = "알림 이름은 null일 수 없습니다.")
    @NotBlank(message = "알림 이름은 필수입니다.")
    private String name;

    @NotNull(message = "알림 시간은 null일 수 없습니다.")
    @NotBlank(message = "알림 시간은 필수입니다.")
    private Date time;
}