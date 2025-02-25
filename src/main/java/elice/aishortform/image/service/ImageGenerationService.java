package elice.aishortform.image.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import elice.aishortform.image.dto.ImageGenerationResponseDto.ImageDto;
import elice.aishortform.image.entity.Image;
import elice.aishortform.summary.entity.Summary;
import elice.aishortform.global.config.ApiConfig;
import elice.aishortform.image.repository.ImageRepository;
import elice.aishortform.summary.service.SummarizeService;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final SummarizeService summarizeService;
    private final ImageRepository imageRepository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client;
    private final ApiConfig apiConfig;
    private static final String API_URL = "https://api-cloud-function.elice.io/0133c2f7-9f3f-44b6-a3d6-c24ba8ef4510/generate";
    private static final String UPLOAD_DIR = "uploads/";

    @Value("${springapi.url}")
    private String serverUrl;

    @Async
    public CompletableFuture<ImageDto> generateImageAsync(String paragraph, String style) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("📌 비동기 이미지 생성 시작 (paragraph={}) - thread:{}", paragraph, Thread.currentThread().getName());

            String imageId = generateUniqueImageId();
            String base64Image = fetchImages(paragraph, style);

            if (base64Image == null) {
                log.error("❌ 이미지 생성 실패: 문단 {}",paragraph);
                throw new RuntimeException("이미지 생성 실패");
            }

            String imageUrl = saveImage(base64Image, imageId);
            imageRepository.save(new Image(imageId, imageUrl));

            log.info("✅ 비동기 이미지 생성 완료 (imageId={}, url={})",imageId,imageUrl);
            return new ImageDto(imageId, imageUrl);
        });
    }

    public List<ImageDto> generateImages(Long summaryId, String style) {
        // summary_id에 해당하는 문단들 가져오기
        Summary summary = summarizeService.getSummaryById(summaryId);
        summarizeService.updateSummaryStyle(summaryId, style);

        List<String> paragraphs = summary.getParagraphs();
        Map<Integer, String> paragraphImageMap = summary.getParagraphImageMap();
        if (paragraphImageMap == null) {
            paragraphImageMap = new HashMap<>();
        }

        List<CompletableFuture<ImageDto>> futureList = new ArrayList<>();
        for (String paragraph: paragraphs) {
            futureList.add(generateImageAsync(paragraph,style));
        }

        List<ImageDto> images = futureList.stream()
                .map(CompletableFuture::join)
                .toList();

        for (int i=0; i<images.size(); i++) {
            paragraphImageMap.put(i, images.get(i).getImageId());
        }

        summary = new Summary(summary.getSummaryId(), summary.getSummaryText(), summary.getParagraphs(),
                paragraphImageMap, summary.getPlatform(), summary.getVoice(), summary.getStyle());
        summarizeService.updateSummary(summary);

        log.info("✅ 비동기 이미지 생성 완료 (총 {}개)", images.size());
        return images;
    }

    public ImageDto regenerateImage(String imageId) {
        imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지 ID입니다: " + imageId));

        Summary summary = summarizeService.getSummaryByImageId(imageId);
        Integer paragraphIndex = summary.getParagraphImageMap().entrySet().stream()
                .filter(entry -> entry.getValue().equals(imageId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이미지와 연결된 문단을 찾을 수 없습니다."));

        String paragraphText = summary.getParagraphs().get(paragraphIndex);

        String base64Image = fetchImages(paragraphText, summary.getStyle());
        String newImageUrl = saveImage(base64Image, imageId);

        Image newImage = new Image(imageId, newImageUrl);
        imageRepository.save(newImage);
        summary.getParagraphImageMap().put(paragraphIndex, imageId);
        summarizeService.updateSummary(summary);

        return new ImageDto(imageId, newImageUrl);
    }

    private String fetchImages(String prompt, String style) {
        log.info("📌 이미지 생성 요청 ({}-style={})",prompt,style);

        try {
            Map<String, Object> requestData = createImageRequestData(prompt, style);
            String responseBody = sendRequest(requestData);
            return extractImage(responseBody);
        } catch (IOException e) {
            log.error("❌ 이미지 생성 API 요청 실패",e);
            throw new RuntimeException("이미지 생성 API 요청 실패",e);
        }
    }

    private Map<String, Object> createImageRequestData(String prompt, String style) {
        return Map.of(
                "prompt", prompt,
                "style", style
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

        int retryCount = 0;
        int maxRetries = 5; // 최대 5번 재시도
        int waitTime = 2000; // 초기 대기 시간 2초

        while (retryCount < maxRetries) {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    return response.body().string();
                } else if (response.code() == 429) { // 429 Too Many Requests 처리
                    log.warn("🚨 429 Too Many Requests - {}ms 후 재시도", waitTime);
                    Thread.sleep(waitTime);
                    waitTime *= 2; // 지수적 증가 (2초 → 4초 → 8초 → 16초 → 32초)
                    retryCount++;
                } else {
                    log.error("❌ API 요청 실패: {} - {}", response.code(), response.message());
                    throw new IOException("API 요청 실패: " + response.code());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("재시도 중 인터럽트 발생", e);
            }
        }

        throw new IOException("API 요청 실패 (최대 재시도 횟수 초과)");
    }

    private String extractImage(String responseBody) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.path("predictions").asText();
    }

    private String saveImage(String base64Image, String imageId) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

            String filePath = UPLOAD_DIR + imageId + ".png";
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedBytes);
            }

            log.info("✅ 이미지 저장 완료 (filePath={})",filePath);
            return serverUrl + "/uploads/" + imageId + ".png";
        } catch (Exception e) {
            log.error("❌ 이미지 저장 실패");
            throw new RuntimeException("이미지 저장 중 오류 발생",e);
        }
    }

    private String generateUniqueImageId() {
        return "img_" + UUID.randomUUID().toString().substring(0,8);
    }

}
