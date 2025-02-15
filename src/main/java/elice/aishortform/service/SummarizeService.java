package elice.aishortform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import elice.aishortform.dto.SummarizeRequest;
import elice.aishortform.dto.SummarizeResponse;
import elice.aishortform.global.config.ApiConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    private final ApiConfig apiConfig;

    private static final String API_URL = "https://api-cloud-function.elice.io/9f071d94-a459-429d-a375-9601e521b079/v1/chat/completions";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SummarizeResponse summarize(SummarizeRequest request){
        log.info("📌 크롤링 요청 URL: {}, 플랫폼: {}",request.getUrl(),request.getPlatform());

        // python 크롤링 요청
        // 현재 임의로 지정
        String crawledContent = "클라이언트와 서버 간의 요청과 응답 상태를 나타낸다. 크게 100번대에서 500번대의 상태 코드가 있다.";

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
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model","helpy-pro");
            requestData.put("sess_id", UUID.randomUUID().toString());

            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role","system");
            systemMessage.put("content","이 내용을 정리해줘. 한 문장 한 문장 사람한테 설명해주듯이 얘기해줘.");
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role","user");
            userMessage.put("content",userContent);
            messages.add(userMessage);

            requestData.put("messages",messages);

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
                    return "Error: " + response.code() + " " + response.message();
                }
                assert response.body() != null;
                String responseBody = response.body().string();
                log.info("✅ API response received successfully");

                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.path("choices").get(0).path("message").path("content").asText();
            } catch (IOException e) {
                log.error("❌ API request failed",e);
                return "Error: " + e.getMessage();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
