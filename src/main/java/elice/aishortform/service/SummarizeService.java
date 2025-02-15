package elice.aishortform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import elice.aishortform.dto.SummarizeRequest;
import elice.aishortform.dto.SummarizeResponse;
import elice.aishortform.global.config.ApiConfig;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizeService {

    private final CrawlingService crawlingService;
    private final ApiConfig apiConfig;
    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_URL = "https://api-cloud-function.elice.io/9f071d94-a459-429d-a375-9601e521b079/v1/chat/completions";
    private static final String SYSTEM_MESSAGE = "이 내용을 정리해줘. 한 문장 한 문장 사람한테 설명해주듯이 얘기해줘. 개행이나 특수 부호 없이 글자만 있게해줘.";

    public SummarizeResponse summarize(SummarizeRequest request){
        log.info("📌 크롤링 요청 URL: {}, 플랫폼: {}",request.getUrl(),request.getPlatform());

        // python 크롤링 요청
        String crawledContent = crawlingService.fetchCrawledContent(request.getUrl());

        String summaryText = fetchSummary(crawledContent);

        Long summaryId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        return SummarizeResponse.builder()
                .summaryId(summaryId)
                .summaryText(summaryText)
                .paragraphs(List.of(summaryText)) // 문단별 분리 구현해야함
                .platform(request.getPlatform())
                .build();
    }

    private String fetchSummary(String userContent) {
        log.info("📌 요약 요청 시작");

        try {
            Map<String, Object> requestData = createSummaryRequestData(userContent);

            String responseBody = sendRequest(requestData);

            return extractSummaryContent(responseBody);
        } catch (Exception e) {
            log.error("❌ 요약 API 요청 실패", e);
            return "요약 API 오류 발생";
        }
    }

    // 요약 API 요청 데이터 생성
    private Map<String, Object> createSummaryRequestData(String userContent) {
        return Map.of(
                "model", "helpy-pro",
                "sess_id", UUID.randomUUID().toString(),
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_MESSAGE),
                        Map.of("role", "user", "content", userContent)
                )
        );
    }

    // OkHttp를 사용하여 API 요청 전송
    private String sendRequest(Map<String, Object> requestData) throws IOException {
        String jsonRequestBody = objectMapper.writeValueAsString(requestData);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonRequestBody, okhttp3.MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization", apiConfig.getKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("❌ API call failed with status: {} - {}",response.code(),response.message());
                throw new IOException("API 요청 실패: " + response.code());
            }
            assert response.body() != null;
            return response.body().string();
        }
    }

    // JSON 응답에서 content 값 추출
    private String extractSummaryContent(String responseBody) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.path("choices").get(0).path("message").path("content").asText();
    }
}
