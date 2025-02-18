package elice.aishortform.controller;

import elice.aishortform.dto.ImageGenerationRequestDto;
import elice.aishortform.dto.ImageGenerationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ImageApiDocs {

    @Operation(
            summary = "문단 전체 이미지 생성",
            description = "주어진 summary_id를 기반으로 문단별 이미지를 생성합니다. " +
                    "- 요청 본문: summaryId (Long) " +
                    "- 응답: 생성된 이미지 목록과 개수 " +
                    "- 예외 처리: summaryId가 존재하지 않으면 IllegalArgumentException 발생"
    )
    @PostMapping("/generate-all")
    ResponseEntity<ImageGenerationResponseDto> generateImages(@RequestBody ImageGenerationRequestDto request);
}
