package elice.aishortform.image.controller;

import elice.aishortform.image.dto.ImageGenerationRequestDto;
import elice.aishortform.image.dto.ImageGenerationResponseDto;
import elice.aishortform.image.dto.ImageGenerationResponseDto.ImageDto;
import elice.aishortform.image.dto.TestImageGenerationRequestDto;
import elice.aishortform.image.service.ImageGenerationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
        List<ImageDto> images = imageGenerationService.generateImages(request.summaryId(), request.style());
        return ResponseEntity.ok(new ImageGenerationResponseDto(images, images.size()));
    }

    public ResponseEntity<ImageDto> regenerateImage(@PathVariable("image_id") String imageId) {
        ImageDto newImage = imageGenerationService.regenerateImage(imageId);
        return ResponseEntity.ok(newImage);
    }

    public ResponseEntity<ImageGenerationResponseDto.ImageDto> generateTestImages(@RequestBody TestImageGenerationRequestDto request) {
        ImageDto image = imageGenerationService.generateTestImages(request.prompt(), request.style());
        return ResponseEntity.ok(image);
    }
}
