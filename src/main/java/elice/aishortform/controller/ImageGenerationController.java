package elice.aishortform.controller;

import elice.aishortform.dto.ImageGenerationRequestDto;
import elice.aishortform.dto.ImageGenerationResponseDto;
import elice.aishortform.dto.ImageGenerationResponseDto.ImageDto;
import elice.aishortform.service.ImageGenerationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageGenerationController {

    private final ImageGenerationService imageGenerationService;

    @PostMapping("/generate-all")
    public ResponseEntity<ImageGenerationResponseDto> generateImages(@RequestBody ImageGenerationRequestDto request) {
        List<ImageDto> images = imageGenerationService.generateImages(request.summaryId());
        return ResponseEntity.ok(new ImageGenerationResponseDto(images, images.size()));
    }
}
