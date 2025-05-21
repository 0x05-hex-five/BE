package hexfive.ismedi.openApi.data.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@ToString
public class Section {
    @XmlElement(name = "ARTICLE")
    private List<Article> articles;
}
