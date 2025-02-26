package elice.aishortform.image.controller;

import elice.aishortform.image.dto.ImageGenerationRequestDto;
import elice.aishortform.image.dto.ImageGenerationResponseDto;
import elice.aishortform.image.dto.ImageGenerationResponseDto.ImageDto;
import elice.aishortform.image.dto.TestImageGenerationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(
            summary = "기존 이미지 재생성",
            description = """
                    주어진 image_id에 대해 새로운 AI 이미지를 생성합니다.\s
                    - 요청: image_id\s
                    - 응답: image_id, image_url\s
                    """
    )
    @PutMapping("/{image_id}/regenerate")
    ResponseEntity<ImageDto> regenerateImage(@PathVariable("image_id") String imageId);

    @Operation(
            summary = "테스트용 단일 이미지 생성",
            description = "임의의 프롬프트와 스타일을 입력하여 단일 이미지를 생성하는 테스트 API입니다."
    )
    @PostMapping("/generate-test-single")
    ResponseEntity<ImageGenerationResponseDto.ImageDto> generateTestImages(@RequestBody TestImageGenerationRequestDto request);
}
