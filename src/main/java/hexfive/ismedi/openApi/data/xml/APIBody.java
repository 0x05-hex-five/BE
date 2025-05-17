package hexfive.ismedi.openApi.data.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import lombok.Getter;

import java.util.List;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class APIBody {
    @XmlElement
    private int pageNo;

    @XmlElement
    private int totalCount;

    @XmlElement
    private int numOfRows;

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<DrugItem> items;
}
