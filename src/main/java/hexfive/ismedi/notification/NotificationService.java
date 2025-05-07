package hexfive.ismedi.notification;

import hexfive.ismedi.category.Category;
import hexfive.ismedi.domain.User;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.notification.dto.NotificationDto;
import hexfive.ismedi.notification.dto.ResNotificationDto;
import hexfive.ismedi.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static hexfive.ismedi.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public ResNotificationDto createNotification(Long userId, NotificationDto notificationDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND, userId));

        Notification notification = Notification.builder()
                .user(user)
                .name(notificationDto.getName())
                .time(notificationDto.getTime())
                .build();
        notificationRepository.save(notification);
        return ResNotificationDto.fromEntity(notification);
    }

    public ResNotificationDto getNotification(Long userId, Long notificationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND, userId));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND, notificationId));
        if (!notification.getUser().equals(user))
            throw new CustomException(UNAUTHORIZED_ACCESS);
        return ResNotificationDto.fromEntity(notification);
    }

    public List<ResNotificationDto> getAllNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(ResNotificationDto::fromEntity)
                .toList();
    }

    public ResNotificationDto updateNotification(Long userId, Long notificationId, NotificationDto notificationDto) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND, notificationId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND, userId));
        if (!notification.getUser().equals(user))
            throw new CustomException(UNAUTHORIZED_ACCESS);

        notification.update(notificationDto);

        notificationRepository.save(notification);

        return ResNotificationDto.fromEntity(notification);
    }

    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND, notificationId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND, userId));
        if (!notification.getUser().equals(user))
            throw new CustomException(UNAUTHORIZED_ACCESS);

        notificationRepository.delete(notification);
    }
}
