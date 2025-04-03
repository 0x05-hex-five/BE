package hexfive.ismedi.domain;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String displayName;

    @Column(nullable = false, length = 255)
    private String selectName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Medicine> medicines;
}