package elice.aishortform.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SummarizeUpdateRequest(
        @JsonProperty("summary_id") Long summaryId,
        @JsonProperty("summary_text") String summaryText
) {
}
