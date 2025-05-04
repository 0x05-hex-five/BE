package hexfive.ismedi.global.swagger;

import hexfive.ismedi.favorites.dto.ResFavoriteDto;
import hexfive.ismedi.global.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface FavoriteDocs {

    @Operation(summary = "즐겨찾기 등록", description = "의약품 ID를 기반으로 해당 의약품을 현재 사용자 즐겨찾기에 추가합니다.")
    APIResponse<String> createFavorites(
            @PathVariable("medicine-id") Long medicineId,
            @Parameter(hidden = true) UserDetails userDetails
    );

    @Operation(summary = "즐겨찾기 전체 조회", description = "현재 로그인한 사용자의 즐겨찾기 목록을 조회합니다.")
    APIResponse<List<ResFavoriteDto>> getAllFavorites(
            @Parameter(hidden = true) UserDetails userDetails
    );

    @Operation(summary = "즐겨찾기 삭제", description = "해당 의약품을 즐겨찾기에서 삭제합니다.")
    APIResponse<String> deleteFavorites(
            @PathVariable("medicine-id") Long medicineId,
            @Parameter(hidden = true) UserDetails userDetails
    );

    @Operation(summary = "즐겨찾기 전체 삭제", description = "해당 사용자의 즐겨찾기 목록을 전부 삭제합니다.")
    APIResponse<String> deleteAllFavorites(
            @Parameter(hidden = true) UserDetails userDetails
    );
}
