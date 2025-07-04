package hexfive.ismedi.openApi.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;


@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class OpenAPIResponse<T> {
    @XmlElement(name = "header")
    private OpenAPIHeader header;

    @XmlElement(name = "body")
    private OpenAPIBody<T> body;
}
