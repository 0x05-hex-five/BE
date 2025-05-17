package hexfive.ismedi.openApi.data.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
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
