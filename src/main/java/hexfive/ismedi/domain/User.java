package hexfive.ismedi.domain;

import hexfive.ismedi.favorites.Favorite;
import hexfive.ismedi.notification.Notification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column
    private Boolean pregnant;

    @Column(nullable = false)
    private Boolean alert;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @Column(nullable = true)
    private String fcmToken;

    public enum Gender {
        WOMAN,
        MAN;
    }

    public void updateUserInfo(LocalDate birth, Gender gender, Boolean pregnant, Boolean alert) {
        this.birth = birth;
        this.gender = gender;
        this.pregnant = pregnant;
        this.alert = alert;
    }

    public void setFCMToken(String token) {
        this.fcmToken = token;
    }
}
