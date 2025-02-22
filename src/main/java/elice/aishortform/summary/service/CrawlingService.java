package elice.aishortform.summary.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final String fastApiUrl;
    private final RestTemplate restTemplate;

    public CrawlingService(@Value("${fastapi.url}") String fastApiUrl) {
        this.fastApiUrl = fastApiUrl;
        this.restTemplate = new RestTemplate();
    }

    public String fetchCrawledContent(String blogUrl) {
        String apiUrl = fastApiUrl + "/crawl";

    public String fetchCrawledContent(String blogUrl) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("url",blogUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl,request,Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return (String) Objects.requireNonNull(response.getBody()).get("blog_content");
        } else {
            return "❌ 크롤링 실패";
        }
    }
}
