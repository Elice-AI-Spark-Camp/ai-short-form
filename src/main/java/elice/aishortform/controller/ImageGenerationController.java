package elice.aishortform.controller;

import elice.aishortform.dto.ImageGenerationRequestDto;
import elice.aishortform.dto.ImageGenerationResponseDto;
import elice.aishortform.dto.ImageGenerationResponseDto.ImageDto;
import elice.aishortform.service.ImageGenerationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Image API", description = "이미지 생성 및 조회 API")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageGenerationController implements ImageApiDocs {

    private final ImageGenerationService imageGenerationService;

    public ResponseEntity<ImageGenerationResponseDto> generateImages(@RequestBody ImageGenerationRequestDto request) {
        List<ImageDto> images = imageGenerationService.generateImages(request.summaryId());
        return ResponseEntity.ok(new ImageGenerationResponseDto(images, images.size()));
    }
}
