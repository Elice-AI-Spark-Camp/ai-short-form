package elice.aishortform.summary.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class CrawlingService {

    private static final String PYTHON_CRAWLER_URL = "http://127.0.0.1:5001/crawl/";

    private final RestTemplate restTemplate;

    public String fetchCrawledContent(String blogUrl) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("url",blogUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(PYTHON_CRAWLER_URL,request,Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return (String) Objects.requireNonNull(response.getBody()).get("blog_content");
        } else {
            return "❌ 크롤링 실패";
        }
    }
}
