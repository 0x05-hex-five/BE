package hexfive.ismedi.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hexfive.ismedi.notification.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResNotificationDto {
    private Long id;

    private String name;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "알림 시간 (HH:mm 형식)", example = "09:30", type = "string")
    private LocalTime time;

    public static ResNotificationDto fromEntity(Notification notification) {
        return ResNotificationDto.builder()
                .id(notification.getId())
                .name(notification.getName())
                .time(notification.getTime())
                .build();
    }
}