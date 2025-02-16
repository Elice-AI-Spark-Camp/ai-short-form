package elice.aishortform.controller;

import elice.aishortform.dto.SummarizeRequest;
import elice.aishortform.dto.SummarizeResponse;
import elice.aishortform.service.SummarizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
@RequiredArgsConstructor
public class SummarizeController {

    private final SummarizeService summarizeService;

    @PostMapping
    public SummarizeResponse summarize(@RequestBody SummarizeRequest request) {
        return summarizeService.summarize(request);
    }
}
