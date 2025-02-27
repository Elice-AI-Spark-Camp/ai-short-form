package elice.aishortform.summary.controller;

import elice.aishortform.summary.dto.SummarizeRequest;
import elice.aishortform.summary.dto.SummarizeResponse;
import elice.aishortform.summary.dto.SummarizeUpdateRequest;
import elice.aishortform.summary.dto.VoiceRequestDto;
import elice.aishortform.summary.dto.VoiceResponseDto;
import elice.aishortform.summary.entity.Summary;
import elice.aishortform.summary.service.SummarizeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Summary API", description = "크롤링 및 요약 API")
@RestController
@RequestMapping("/summary")
@RequiredArgsConstructor
public class SummarizeController implements SummarizeApiDocs{

    private final SummarizeService summarizeService;

    public ResponseEntity<SummarizeResponse> summarize(@RequestBody SummarizeRequest request) {
        return ResponseEntity.ok(summarizeService.summarize(request));
    }

    public ResponseEntity<Summary> updateSummary(@RequestBody SummarizeUpdateRequest request) {
        Summary updatedSummary = summarizeService.updateSummary(request);
        return ResponseEntity.ok(updatedSummary);
    }

    public ResponseEntity<VoiceResponseDto> selectVoice(
            @PathVariable("summary_id") Long summaryId,
            @RequestBody VoiceRequestDto request
    ) {
        summarizeService.updateTtsVoice(summaryId, request.voice());
        return ResponseEntity.ok(new VoiceResponseDto("음성 선택 완료"));
    }
}
