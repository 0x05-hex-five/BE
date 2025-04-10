package hexfive.ismedi.openApi.data.prescriptionType;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PrescriptionType {
    @Id
    @JsonProperty("ITEM_SEQ")
    private String itemSeq;

    @JsonProperty("ENTP_NAME")
    private String entpName;

    @Column(length = 1000)
    @JsonProperty("ITEM_NAME")
    private String itemName;

    @JsonProperty("ITEM_PERMIT_DATE")
    private String itemPermitDate;

    @JsonProperty("VALID_TERM_DATE")
    private String validTermDate;

    @JsonProperty("VALID_TERM_DATE_CUTLINE")
    private String validTermDateCutline;

    @JsonProperty("INDUTY_CODE_NAME")
    private String indutyCodeName;

    @JsonProperty("INDUTY_CODE")
    private String indutyCode;

    @JsonProperty("ITEM_NO")
    private String itemNo;

    @JsonProperty("ETC_OTC_CODE_NAME")
    private String etcOtcCodeName;

    @JsonProperty("CLASS_NO_NAME")
    private String classNoName;

    @JsonProperty("PERMIT_KIND_CODE_NAME")
    private String permitKindCodeName;

    @JsonProperty("BIZRNO")
    private String bizrno;
}
