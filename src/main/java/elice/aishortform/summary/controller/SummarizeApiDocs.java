package elice.aishortform.summary.controller;

import elice.aishortform.summary.dto.SummarizeRequest;
import elice.aishortform.summary.dto.SummarizeResponse;
import elice.aishortform.summary.dto.SummarizeUpdateRequest;
import elice.aishortform.summary.entity.Summary;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SummarizeApiDocs {

    @Operation(
            summary = "요약 생성",
            description = "주어진 URL과 플랫폼 정보를 기반으로 콘텐츠를 크롤링하고 요약을 생성합니다.")
    @PostMapping
    SummarizeResponse summarize(@RequestBody SummarizeRequest request);

    @Operation(
            summary = "요약 업데이트",
            description = "기존 요약 내용을 새로운 텍스트로 업데이트하며 <br> 태그 기준으로 문단을 자동 분리합니다."
    )
    @PutMapping
    ResponseEntity<Summary> updateSummary(@RequestBody SummarizeUpdateRequest request);

    @Operation(
            summary = "TTS 음성 선택",
            description = "사용자가 원하는 AI 음성을 선택합니다."
    )
    @PatchMapping("/{summary_id}/tts")
    ResponseEntity<Map<String, String>> selectVoice(
            @PathVariable("summary_id") Long summaryId,
            @RequestBody Map<String, String> request
    );
}

