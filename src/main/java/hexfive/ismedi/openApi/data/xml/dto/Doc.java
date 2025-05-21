package hexfive.ismedi.openApi.data.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.ToString;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@ToString
public class Doc {
    @XmlAttribute(name = "title")
    private String title;

    @XmlAttribute(name = "type")
    private String type;

    @XmlElement(name = "SECTION")
    private Section section;
}
