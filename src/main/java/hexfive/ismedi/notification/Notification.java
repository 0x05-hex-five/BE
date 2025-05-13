package hexfive.ismedi.notification;
import hexfive.ismedi.domain.User;
import hexfive.ismedi.notification.dto.NotificationDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalTime time;

    public void update(NotificationDto notificationDto) {
        this.name = notificationDto.getName();
        this.time = notificationDto.getTime();
    }
}