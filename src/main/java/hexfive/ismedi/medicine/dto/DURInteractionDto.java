package hexfive.ismedi.medicine.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DURInteractionDto {
    @JsonProperty("ITEM_SEQ")
    private String itemSeq; // 품목기준코드

    @JsonProperty("ITEM_NAME")
    private String itemName; // 품목명

    @JsonProperty("PROHBT_CONTENT")
    private String prohbtContent; // 금기내용

    // ----------

    @JsonProperty("INGR_CODE")
    private String ingrCode; // DUR성분코드

    @JsonProperty("INGR_KOR_NAME")
    private String ingrKorName; // DUR성분

    @JsonProperty("DUR_SEQ")
    private String durSeq; // DUR일련번호

    @JsonProperty("TYPE_CODE")
    private String typeCode; // DUR유형코드

    @JsonProperty("TYPE_NAME")
    private String typeName; // DUR유형

    @JsonProperty("MIX")
    private String mix; // 단일/복합

    @JsonProperty("INGR_ENG_NAME")
    private String ingrEngName; // DUR성분(영문)

    @JsonProperty("MIX_INGR")
    private String mixIngr; // 복합제

    @JsonProperty("ENTP_NAME")
    private String entpName; // 업체명

    @JsonProperty("CHART")
    private String chart; // 성상

    @JsonProperty("FORM_CODE")
    private String formCode; // 제형구분코드

    @JsonProperty("ETC_OTC_CODE")
    private String etcOtcCode; // 전문일반 구분코드

    @JsonProperty("CLASS_CODE")
    private String classCode; // 약효분류코드

    @JsonProperty("FORM_NAME")
    private String formName; // 제형

    @JsonProperty("ETC_OTC_NAME")
    private String etcOtcName; // 전문/일반

    @JsonProperty("CLASS_NAME")
    private String className; // 약효분류

    @JsonProperty("MAIN_INGR")
    private String mainIngr; // 주성분

    @JsonProperty("MIXTURE_DUR_SEQ")
    private String mixtureDurSeq; // 병용금기DUR번호

    @JsonProperty("MIXTURE_MIX")
    private String mixtureMix; // 병용금기복합제

    @JsonProperty("MIXTURE_INGR_CODE")
    private String mixtureIngrCode; // 병용금기DUR성분코드

    @JsonProperty("MIXTURE_INGR_KOR_NAME")
    private String mixtureIngrKorName; // 병용금기DUR성분

    @JsonProperty("MIXTURE_INGR_ENG_NAME")
    private String mixtureIngrEngName; // 병용금기DUR성분(영문)

    @JsonProperty("MIXTURE_ITEM_SEQ")
    private String mixtureItemSeq; // 병용금기품목기준코드

    @JsonProperty("MIXTURE_ITEM_NAME")
    private String mixtureItemName; // 병용금기품목명

    @JsonProperty("MIXTURE_ENTP_NAME")
    private String mixtureEntpName; // 병용금기업체명

    @JsonProperty("MIXTURE_FORM_CODE")
    private String mixtureFormCode; // 병용금기제형구분코드

    @JsonProperty("MIXTURE_ETC_OTC_CODE")
    private String mixtureEtcOtcCode; // 병용금기전문일반구분코드

    @JsonProperty("MIXTURE_CLASS_CODE")
    private String mixtureClassCode; // 병용금기약효분류코드

    @JsonProperty("MIXTURE_FORM_NAME")
    private String mixtureFormName; // 병용금기제형

    @JsonProperty("MIXTURE_ETC_OTC_NAME")
    private String mixtureEtcOtcName; // 병용금기전문/일반

    @JsonProperty("MIXTURE_CLASS_NAME")
    private String mixtureClassName; // 병용금기약효분류

    @JsonProperty("MIXTURE_MAIN_INGR")
    private String mixtureMainIngr; // 병용금기주성분

    @JsonProperty("NOTIFICATION_DATE")
    private String notificationDate; // 고시일자

    @JsonProperty("REMARK")
    private String remark; // 비고

    @JsonProperty("ITEM_PERMIT_DATE")
    private String itemPermitDate; // 품목허가일자

    @JsonProperty("MIXTURE_ITEM_PERMIT_DATE")
    private String mixtureItemPermitDate; // 병용금기품목허가일자

    @JsonProperty("MIXTURE_CHART")
    private String mixtureChart; // 병용금기성상

    @JsonProperty("CHANGE_DATE")
    private String changeDate; // 변경일자

    @JsonProperty("MIXTURE_CHANGE_DATE")
    private String mixtureChangeDate; // 병용변경일자

    @JsonProperty("BIZRNO")
    private String bizrno; // 사업자등록번호
}