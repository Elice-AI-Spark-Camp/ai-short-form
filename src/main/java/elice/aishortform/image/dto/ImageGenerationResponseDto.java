package elice.aishortform.image.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageGenerationResponseDto {
    private List<ImageDto> images;

    @JsonProperty("total_images")
    private int totalImages;

    @Getter
    @AllArgsConstructor
    public static class ImageDto {
        @JsonProperty("image_id")
        private String imageId;

        @JsonProperty("image_url")
        private String imageUrl;
    }
}
