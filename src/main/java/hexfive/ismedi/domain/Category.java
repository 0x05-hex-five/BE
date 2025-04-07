package hexfive.ismedi.domain;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String selectName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Medicine> medicines;

    @Builder
    public Category(String displayName, String selectName) {
        this.displayName = displayName;
        this.selectName = selectName;
    }

    public void update(String displayName, String selectName) {
        this.displayName = displayName;
        this.selectName = selectName;
    }
}