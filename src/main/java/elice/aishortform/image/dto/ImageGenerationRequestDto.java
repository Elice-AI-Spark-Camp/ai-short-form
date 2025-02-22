package elice.aishortform.image.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageGenerationRequestDto(
        @JsonProperty("summary_id") Long summaryId,
        @JsonProperty("style") String style
        ){
}
