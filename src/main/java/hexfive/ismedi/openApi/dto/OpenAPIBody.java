package hexfive.ismedi.openApi.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class OpenAPIBody<T> {
    @XmlElement
    private int pageNo;

    @XmlElement
    private int totalCount;

    @XmlElement
    private int numOfRows;

    @XmlElement
    private List<T> items;

    public List<T> getItems() {
        return items != null ? items : Collections.emptyList();
    }
}
