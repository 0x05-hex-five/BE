package hexfive.ismedi.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import hexfive.ismedi.category.Category;
import hexfive.ismedi.domain.User;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.notification.dto.NotificationDto;
import hexfive.ismedi.notification.dto.ResNotificationDto;
import hexfive.ismedi.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static hexfive.ismedi.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@EnableScheduling
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

    private void sendNotification(Notification notification) {
        String token = notification.getUser().getFcmToken();
        if (token == null || token.isBlank()) {
            // 토큰이 없을 경우 처리
            log.warn("FCM 토큰이 존재하지 않습니다. 사용자 ID: {}", notification.getUser().getId());
            return;
        }

        String title = "복약 알림";
        String formattedTime = notification.getTime()
                .format(DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREAN));
        String body = String.format("지금은 [%s] 복용 시간입니다 (%s). 잊지 말고 챙겨 드세요!", notification.getName(), formattedTime);

        Message message = Message.builder()
                .setToken(token)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("알림 메시지 전송 성공: {}", response);
        } catch (Exception e) {
            log.error("FCM 메시지 전송 실패", e);
        }
    }

//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(fixedRate = 60000) // 60,000ms = 1분
    public void sendScheduledNotifications() {
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0); // 초 단위는 무시한 현재 시간
        log.info(String.valueOf(currentTime));
        List<Notification> notifications = notificationRepository.findByTime(currentTime);
        for (Notification notification : notifications) {
            sendNotification(notification);
        }
    }
}
