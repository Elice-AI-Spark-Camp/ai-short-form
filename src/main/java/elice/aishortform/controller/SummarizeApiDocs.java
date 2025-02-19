package elice.aishortform.controller;

import elice.aishortform.dto.SummarizeRequest;
import elice.aishortform.dto.SummarizeResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SummarizeApiDocs {

    @Operation(
            summary = "요약 생성",
            description = "주어진 URL과 플랫폼 정보를 기반으로 콘텐츠를 크롤링하고 요약을 생성합니다.")
    @PostMapping
    SummarizeResponse summarize(@RequestBody SummarizeRequest request);
}
