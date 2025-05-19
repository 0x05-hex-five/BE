package hexfive.ismedi.openApi.data.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.ToString;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@ToString
public class DocData {
    @XmlElement(name = "DOC")
    private Doc doc;
}