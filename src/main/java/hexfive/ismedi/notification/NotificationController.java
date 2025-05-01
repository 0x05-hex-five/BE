package hexfive.ismedi.notification;

import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.notification.dto.NotificationDto;
import hexfive.ismedi.notification.dto.ResNotificationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public APIResponse<ResNotificationDto> createNotification(@AuthenticationPrincipal UserDetails userDetails,
                                                              @Valid @RequestBody NotificationDto createNotificationDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return APIResponse.success(notificationService.createNotification(userId, createNotificationDto));
    }

    @GetMapping("/{id}")
    public APIResponse<ResNotificationDto> getNotification(@AuthenticationPrincipal UserDetails userDetails,
                                                           @PathVariable Long id) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return APIResponse.success(notificationService.getNotification(userId, id));
    }

    @GetMapping
    public APIResponse<List<ResNotificationDto>> getAllNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return APIResponse.success(notificationService.getAllNotifications(userId));
    }
}