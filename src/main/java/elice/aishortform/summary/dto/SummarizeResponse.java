package elice.aishortform.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SummarizeResponse {

    @JsonProperty("summary_id")
    private Long summaryId;

    @JsonProperty("summary_text")
    private String summaryText;

    private List<String> paragraphs;
    private String platform;
}
