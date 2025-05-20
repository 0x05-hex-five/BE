package hexfive.ismedi.openApi.data.ImageAndClass;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class ImageAndClass {
    @Id
    @JsonProperty("ITEM_SEQ")
    private String itemSeq;

    @JsonProperty("ITEM_IMAGE")
    private String itemImage;

    @Column(length = 1000)
    @JsonProperty("CLASS_NAME")
    private String className;
}
