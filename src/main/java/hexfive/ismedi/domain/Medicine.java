package hexfive.ismedi.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Medicine")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String entpName;

    @Column
    private String efficacy;

    @Column
    private String method;

    @Column
    private String warning;

    @Column
    private String interaction;

    @Column
    private String sideEffect;

    @Column
    private String storage;

    @Column
    private String image;

    @Column
    private Integer code;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
