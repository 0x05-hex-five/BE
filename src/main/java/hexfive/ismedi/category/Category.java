package hexfive.ismedi.category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String selectName;

//    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
//    private List<Medicine> medicines;

    public void update(String displayName, String selectName) {
        this.displayName = displayName;
        this.selectName = selectName;
    }
}