package hexfive.ismedi.openApi.data.xml.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@ToString
public class DrugItem {

    @XmlElement(name = "ITEM_SEQ")
    private String itemSeq;

    @XmlElement(name = "ITEM_NAME")
    private String itemName;

    @XmlElement(name = "ENTP_NAME")
    private String entpName;

    @XmlElement(name = "ITEM_PERMIT_DATE")
    private String itemPermitDate;

    @XmlElement(name = "CNSGN_MANUF")
    private String cnsGnManuf;

    @XmlElement(name = "ETC_OTC_CODE")
    private String etcOtcCode;

    @XmlElement(name = "CHART")
    private String chart;

    @XmlElement(name = "BAR_CODE")
    private String barCode;

    @XmlElement(name = "MATERIAL_NAME")
    private String materialName;

    @XmlElement(name = "EE_DOC_ID")
    private String eeDocId;

    @XmlElement(name = "UD_DOC_ID")
    private String udDocId;

    @XmlElement(name = "NB_DOC_ID")
    private String nbDocId;

    @XmlElement(name = "INSERT_FILE")
    private String insertFile;

    @XmlElement(name = "STORAGE_METHOD")
    private String storageMethod;

    @XmlElement(name = "VALID_TERM")
    private String validTerm;

    @XmlElement(name = "REEXAM_TARGET")
    private String reexamTarget;

    @XmlElement(name = "REEXAM_DATE")
    private String reexamDate;

    @XmlElement(name = "PACK_UNIT")
    private String packUnit;

    @XmlElement(name = "EDI_CODE")
    private String ediCode;

    @XmlElement(name = "PERMIT_KIND_NAME")
    private String permitKindName;

    @XmlElement(name = "ENTP_NO")
    private String entpNo;

    @XmlElement(name = "MAKE_MATERIAL_FLAG")
    private String makeMaterialFlag;

    @XmlElement(name = "NEWDRUG_CLASS_NAME")
    private String newDrugClassName;

    @XmlElement(name = "INDUTY_TYPE")
    private String indutyType;

    @XmlElement(name = "CANCEL_DATE")
    private String cancelDate;

    @XmlElement(name = "CANCEL_NAME")
    private String cancelName;

    @XmlElement(name = "CHANGE_DATE")
    private String changeDate;

    @XmlElement(name = "NARCOTIC_KIND_CODE")
    private String narcoticKindCode;

    @XmlElement(name = "GBN_NAME")
    private String gbnName;

    @XmlElement(name = "TOTAL_CONTENT")
    private String totalContent;

    @XmlElement(name = "EE_DOC_DATA")
    private DocData eeDocData;

    @XmlElement(name = "UD_DOC_DATA")
    private DocData udDocData;

    @XmlElement(name = "NB_DOC_DATA")
    private DocData nbDocData;
}