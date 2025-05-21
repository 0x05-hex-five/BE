package hexfive.ismedi.openApi.data.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class APIHeader {
    @XmlElement(name = "resultCode")
    private String resultCode;

    @XmlElement(name = "resultMsg")
    private String resultMsg;
}