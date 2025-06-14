package hexfive.ismedi.favorites;

import hexfive.ismedi.favorites.dto.ResFavoriteDto;
import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.global.swagger.FavoriteDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController implements FavoriteDocs {
    private final FavoriteService favoriteService;

    @PostMapping("/{medicine-id}")
    public APIResponse<String> createFavorites(@PathVariable("medicine-id") Long medicineId, @AuthenticationPrincipal UserDetails userDetails){
        Long userId = Long.parseLong(userDetails.getUsername());
        favoriteService.addFavorite(userId, medicineId);
        return APIResponse.success("즐겨찾기에 등록되었습니다.");
    }

    @GetMapping
    public APIResponse<List<ResFavoriteDto>> getAllFavorites(@AuthenticationPrincipal UserDetails userDetails){
        Long userId = Long.parseLong(userDetails.getUsername());
        return APIResponse.success(favoriteService.getFavoriteByUserId(userId));
    }

    @DeleteMapping("/{medicine-id}")
    public APIResponse<String> deleteFavorites(@PathVariable("medicine-id") Long medicineId, @AuthenticationPrincipal UserDetails userDetails){
        Long userId = Long.parseLong(userDetails.getUsername());
        favoriteService.removeFavorite(userId, medicineId);
        return APIResponse.success("즐겨찾기에서 삭제되었습니다.");
    }

    @DeleteMapping
    public APIResponse<String> deleteAllFavorites(@AuthenticationPrincipal UserDetails userDetails){
        Long userId = Long.parseLong(userDetails.getUsername());
        favoriteService.removeAllFavorites(userId);
        return APIResponse.success("즐겨찾기 목록이 전부 삭제되었습니다.");
    }
}
