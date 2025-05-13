package hexfive.ismedi.global.swagger;

import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.notification.dto.NotificationDto;
import hexfive.ismedi.notification.dto.ResNotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "알림 관련 API - 로그인 후 사용 가능")
public interface NotificationDocs {

    @Operation(summary = "알림 생성", description = "알림을 생성합니다.")
    APIResponse<ResNotificationDto> createNotification(
            @Parameter(hidden = true) UserDetails userDetails,
            @RequestBody NotificationDto createNotificationDto);

    @Operation(summary = "단일 알림 조회", description = "알림 ID로 단일 알림을 조회합니다.")
    APIResponse<ResNotificationDto> getNotification(
            @Parameter(hidden = true) UserDetails userDetails,
            @PathVariable Long id);

    @Operation(summary = "모든 알림 조회", description = "해당 사용자의 모든 알림을 조회합니다.")
    APIResponse<List<ResNotificationDto>> getAllNotifications(
            @Parameter(hidden = true) UserDetails userDetails);

    @Operation(summary = "알림 수정", description = "알림 ID로 알림을 수정합니다.")
    APIResponse<ResNotificationDto> updateNotification(
            @Parameter(hidden = true) UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody NotificationDto updateNotificationDto);

    @Operation(summary = "알림 삭제", description = "알림 ID로 알림을 삭제합니다.")
    APIResponse<Void> deleteNotification(
            @Parameter(hidden = true) UserDetails userDetails,
            @PathVariable Long id);
}