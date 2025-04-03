package hexfive.ismedi.domain;
import jakarta.persistence.*;
import java.util.*;

@Entity
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
    private Date time;
}