package elice.aishortform.video.application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import elice.aishortform.image.entity.ImageEntity;
import elice.aishortform.image.repository.ImageRepository;
import elice.aishortform.summary.entity.Summary;
import elice.aishortform.summary.repository.SummaryRepository;
import elice.aishortform.video.domain.model.Video;
import elice.aishortform.video.domain.repository.VideoRepository;
import elice.aishortform.video.dto.VideoRequest;
import elice.aishortform.video.dto.VideoResponse;
import elice.aishortform.video.exception.VideoProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

	private final VideoRepository videoRepository;
	private final WebClient webClient;
	private final SummaryRepository summaryRepository;
	private final ImageRepository imageRepository;

	@Async // 비동기 처리 기능을 활성화하는 어노테이션
	public CompletableFuture<Video> generateVideo(Long summaryId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				// 1. Summary 정보 조회
				Summary summary = summaryRepository.findById(summaryId)
					.orElseThrow(() -> new IllegalArgumentException("Summary not found: " + summaryId));

				// 2. 비디오 객체 생성 및 초기 저장 (PENDING 상태)
				Video video = Video.createNew(summaryId);
				video = videoRepository.save(video);
				log.info("🎬 비디오 생성 요청 시작 - Summary ID: {}", summaryId);

				// 3. 상태를 PROCESSING으로 변경
				video.markProcessing();
				video = videoRepository.save(video);

				// 4. FastAPI 서버에 비디오 생성 요청
				// 이미지 URL 매핑 처리
				Map<String, String> imageUrls = new HashMap<>();
				Map<Integer, String> paragraphImageMap = summary.getParagraphImageMap();
				
				if (paragraphImageMap != null) {
					for (Map.Entry<Integer, String> entry : paragraphImageMap.entrySet()) {
						// 이미지 ID를 가져와서 실제 URL로 변환
						String imageId = entry.getValue();
						Optional<ImageEntity> imageEntity = imageRepository.findById(imageId);
						imageEntity.ifPresent(
							entity -> imageUrls.put(String.valueOf(entry.getKey()), entity.getImageUrl()));
					}
				}

				VideoRequest request = new VideoRequest(
					summaryId,
					summary.getParagraphs(),
					summary.getVoice(),
					imageUrls  // 변환된 이미지 URL 맵
				);

				VideoResponse response = webClient.post()
					.uri("/video/generate")
					.bodyValue(request)
					.retrieve()
					.bodyToMono(VideoResponse.class)
					.block();

				// 5. 응답 처리
				if (response == null || response.videoUrl() == null) {
					video.markFailed();
					videoRepository.save(video);
					throw new VideoProcessingException("FastAPI 서버로부터 유효한 응답을 받지 못했습니다.");
				}

				// 6. 성공 시 상태 업데이트
				video.markCompleted(response.videoUrl());
				video = videoRepository.save(video);
				log.info("✅ 비디오 생성 완료 - URL: {}", response.videoUrl());

				return video;

			} catch (Exception e) {
				log.error("❌ 비디오 생성 실패 - Error: {}", e.getMessage());
				throw new VideoProcessingException("비디오 생성에 실패했습니다.", e);
			}
		}).exceptionally(ex -> {
			log.error("❌ 예외 발생: {}", ex.getMessage(), ex);
			throw new CompletionException(ex);
		});
	}

	public List<Video> getVideosBySummaryId(Long summaryId) {
		return videoRepository.findBySummaryId(summaryId);
	}
}
