package hexfive.ismedi.openApi.data.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@ToString
public class Article {
    @XmlAttribute(name = "title")
    private String title;

    @XmlElement(name = "PARAGRAPH")
    private List<Paragraph> paragraphs;
}
