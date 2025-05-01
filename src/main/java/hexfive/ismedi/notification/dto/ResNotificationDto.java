package hexfive.ismedi.notification.dto;

import hexfive.ismedi.notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResNotificationDto {
    private Long id;
    private String name;
    private Date time;

    public static ResNotificationDto fromEntity(Notification notification) {
        return ResNotificationDto.builder()
                .id(notification.getId())
                .name(notification.getName())
                .time(notification.getTime())
                .build();
    }
}