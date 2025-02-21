package elice.aishortform.summary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import elice.aishortform.summary.dto.SummarizeRequest;
import elice.aishortform.summary.dto.SummarizeResponse;
import elice.aishortform.summary.dto.SummarizeUpdateRequest;
import elice.aishortform.summary.entity.Summary;
import elice.aishortform.global.config.ApiConfig;
import elice.aishortform.summary.repository.SummaryRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizeService {

    private final CrawlingService crawlingService;
    private final ApiConfig apiConfig;
    private final SummaryRepository summaryRepository;
    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_URL = "https://api-cloud-function.elice.io/9f071d94-a459-429d-a375-9601e521b079/v1/chat/completions";
    private static final String SYSTEM_MESSAGE = "이 내용을 정리해줘. 한 문장 한 문장 사람한테 설명해주듯이 얘기해줘. 개행이나 특수 부호 없이 글자만 있게해줘. 장면을 나눠서 <br>태그로 나눠줘.";

    public SummarizeResponse summarize(SummarizeRequest request){
        log.info("📌 크롤링 요청 URL: {}, 플랫폼: {}",request.url(),request.platform());

        // python 크롤링 요청
        String crawledContent = crawlingService.fetchCrawledContent(request.url());

        String summaryText = fetchSummary(crawledContent);
        List<String> paragraphs = Arrays.asList(summaryText.split("<br>"));

        Summary summary = new Summary(
                null,
                summaryText,
                paragraphs,
                Map.of(),
                request.platform(),
                null
        );
        summary = summaryRepository.save(summary);

        return SummarizeResponse.builder()
                .summaryId(summary.getSummaryId())
                .summaryText(summaryText)
                .paragraphs(paragraphs)
                .platform(request.platform())
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

    @Transactional
    public Summary updateSummary(SummarizeUpdateRequest request) {
        Summary summary = summaryRepository.findById(request.summaryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 summary_id가 존재하지 않습니다: " + request.summaryId()));
        List<String> paragraphs = new ArrayList<>(Arrays.asList(request.summaryText().split("<br>")));

        summary.updateText(request.summaryText(),paragraphs);

        return summaryRepository.save(summary);
    }

    @Transactional
    public void updateTtsVoice(Long summaryId, String voice) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 summary_id가 존재하지 않습니다."));

        summary.setVoice(voice);
        summaryRepository.save(summary);

        log.info("✅ 음성 선택 완료 (summaryId={}, voice={})", summaryId, voice);
    }
}
