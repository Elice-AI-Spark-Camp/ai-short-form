package elice.aishortform.controller;

import elice.aishortform.dto.SummarizeRequest;
import elice.aishortform.dto.SummarizeResponse;
import elice.aishortform.service.SummarizeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Summary API", description = "크롤링 및 요약 API")
@RestController
@RequestMapping("/summary")
@RequiredArgsConstructor
public class SummarizeController implements SummarizeApiDocs{

    private final SummarizeService summarizeService;

    public SummarizeResponse summarize(@RequestBody SummarizeRequest request) {
        return summarizeService.summarize(request);
    }
}
