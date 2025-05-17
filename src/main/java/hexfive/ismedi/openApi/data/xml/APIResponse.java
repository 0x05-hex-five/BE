package hexfive.ismedi.openApi.data.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;


@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class APIResponse {
    @XmlElement(name = "header")
    private APIHeader header;

    @XmlElement(name = "body")
    private APIBody body;
}
