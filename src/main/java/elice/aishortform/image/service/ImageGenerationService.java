package elice.aishortform.image.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import elice.aishortform.image.dto.ImageGenerationResponseDto.ImageDto;
import elice.aishortform.image.entity.ImageEntity;
import elice.aishortform.summary.entity.Summary;
import elice.aishortform.global.config.ApiConfig;
import elice.aishortform.image.repository.ImageRepository;
import elice.aishortform.summary.repository.SummaryRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final SummaryRepository summaryRepository;
    private final ImageRepository imageRepository;
    private final ObjectMapper objectMapper;

    private final OkHttpClient client = new OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build();

    private static final String API_URL = "https://api-cloud-function.elice.io/0133c2f7-9f3f-44b6-a3d6-c24ba8ef4510/generate";
    private final ApiConfig apiConfig;

    private static final String UPLOAD_DIR = "uploads/";

    public List<ImageDto> generateImages(Long summaryId) {
        // summary_id에 해당하는 문단들 가져오기
        Summary summary = summaryRepository.findBySummaryId(summaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 summary_id가 존재하지 않습니다: " + summaryId));
        List<String> paragraphs = summary.getParagraphs();
        Map<Integer, String> paragraphImageMap = summary.getParagraphImageMap(); // 기존 맵 가져오기

        if (paragraphImageMap == null) {
            paragraphImageMap = new HashMap<>();
        }

        // 각 문단에 대해 이미지 생성 API 호출
        List<ImageDto> images = new ArrayList<>();
        for (int i = 0; i < paragraphs.size(); i++) {
            String paragraph = paragraphs.get(i);
            String imageId = generateUniqueImageId();
            String base64Image = fetchImages(paragraph);

            // base64 디코딩 -> 파일 저장
            if (base64Image != null) {
                String imageUrl = saveImage(base64Image, imageId);
                imageRepository.save(new ImageEntity(imageId, imageUrl));
                images.add(new ImageDto(imageId, imageUrl));
                paragraphImageMap.put(i, imageId);
            }
        }

        summary = new Summary(summary.getSummaryId(), summary.getSummaryText(), summary.getParagraphs(), paragraphImageMap, summary.getPlatform());
        summaryRepository.save(summary);

        log.info("✅ 이미지 생성 완료 (총 {}개)",images.size());
        return images;
    }

    private String fetchImages(String prompt) {
        log.info("📌 이미지 생성 요청 ({})",prompt);

        try {
            Map<String, Object> requestData = createImageRequestData(prompt);

            String responseBody = sendRequest(requestData);

            return extractImage(responseBody);
        } catch (IOException e) {
            log.error("❌ 이미지 생성 API 요청 실패",e);
            return null;
        }
    }

    private Map<String, Object> createImageRequestData(String prompt) {
        return Map.of(
                "prompt", prompt,
                "style", "polaroid"
        );
    }

    private String sendRequest(Map<String, Object> requestData) throws IOException {
        String jsonRequestBody = objectMapper.writeValueAsString(requestData);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonRequestBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Accept","application/json")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization",apiConfig.getKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("❌ API 요청 실패");
                throw new IOException("API 요청 실패: " + response.code());
            }
            assert response.body() != null;
            return response.body().string();
        }
    }

    private String extractImage(String responseBody) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.path("predictions").asText();
    }

    private String saveImage(String base64Image, String imageId) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            File uploadDir = new File(UPLOAD_DIR);

            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String filePath = UPLOAD_DIR + imageId + ".png";
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedBytes);
            }

            log.info("✅ 이미지 저장 완료 (filePath={})",filePath);
            return "http://localhost:8080/uploads/" + imageId + ".png";
        } catch (Exception e) {
            log.error("❌ 이미지 저장 실패");
            return null;
        }
    }

    private String generateUniqueImageId() {
        return "img_" + UUID.randomUUID().toString().substring(0,8);
    }

}
