package hexfive.ismedi.openApi.data.ImageAndClass;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class ImageAndClass {
    @JsonProperty("ITEM_SEQ")
    private String itemSeq;

    @JsonProperty("ITEM_IMAGE")
    private String itemImage;

    @Column(length = 1000)
    @JsonProperty("CLASS_NAME")
    private String className;

}
