package hexfive.ismedi.domain;
import jakarta.persistence.*;
import java.util.*;

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
    private Date birth;

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

    public enum Gender {
        WOMAN,
        MAN;
    }
}
