package hexfive.ismedi.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PrescriptionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entpName;
    @Column(length = 1000)
    private String itemName;
    private String itemPermitDate;
    private String validTermDate;
    private String validTermDateCutline;
    private String indutyCodeName;
    private String indutyCode;
    private String itemNo;
    private String itemSeq;
    private String etcOtcCodeName;
    private String classNoName;
    private String permitKindCodeName;
    private String bizrno;
}
