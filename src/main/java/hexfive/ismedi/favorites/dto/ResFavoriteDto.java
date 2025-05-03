package hexfive.ismedi.favorites.dto;

import hexfive.ismedi.favorites.Favorite;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResFavoriteDto {

    private Long medicineId;
    private String medicineName;

    public static ResFavoriteDto fromEntity(Favorite favorite) {
        return ResFavoriteDto.builder()
                .medicineId(favorite.getMedicine().getId())
                .medicineName(favorite.getMedicine().getItemName())
                .build();
    }
}
