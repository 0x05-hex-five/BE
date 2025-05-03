package hexfive.ismedi.global.swagger;

import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.medicine.dto.ResMedicineDetailDto;
import hexfive.ismedi.medicine.dto.ResMedicineDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface MedicineDocs {

    @Operation(summary = "의약품 데이터 병합", description = "공공데이터 API에서 받아온 의약품 정보를 DB에 병합합니다.")
    @GetMapping("/init")
    void mergeMedicineData();

    @Operation(summary = "의약품 목록 조회", description = "이름과 종류(전문의약품/일반의약품)에 따라 의약품을 검색합니다. 약 이름에 검색 키워드가 포함된 모든 약의 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "의약품 목록 조회 성공")
    })
    @GetMapping("")
    APIResponse<List<ResMedicineDto>> searchMedicines(
            @Parameter(description = "검색할 의약품 이름", example = "타이레놀")
            @RequestParam(required = false, defaultValue = "") String name,
            @Parameter(description = "의약품 종류 (ALL, ETC - 전문의약품, OTC - 일반의약품)", example = "ALL")
            @RequestParam(defaultValue = "ALL") String type
    );

    @Operation(summary = "의약품 상세 조회", description = "의약품 ID로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "의약품 상세 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 의약품이 존재하지 않음")
    })
    @GetMapping("/{id}")
    APIResponse<ResMedicineDetailDto> searchMedicines(
            @Parameter(description = "의약품 ID", example = "1")
            @PathVariable Long id
    );
}
