package hexfive.ismedi.openApi.data.xml.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.ToString;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@ToString
public class Paragraph {
    @XmlAttribute(name = "tagName")
    private String tagName;

    @XmlAttribute(name = "textIndent")
    private String textIndent;

    @XmlAttribute(name = "marginLeft")
    private String marginLeft;

    @XmlValue
    private String text;
}
