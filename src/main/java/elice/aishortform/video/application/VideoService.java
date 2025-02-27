package elice.aishortform.video.application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import elice.aishortform.image.entity.Image;
import elice.aishortform.image.repository.ImageRepository;
import elice.aishortform.summary.entity.Summary;
import elice.aishortform.summary.repository.SummaryRepository;
import elice.aishortform.video.domain.model.Video;
import elice.aishortform.video.domain.repository.VideoRepository;
import elice.aishortform.video.dto.VideoRequest;
import elice.aishortform.video.dto.VideoResponse;
import elice.aishortform.video.exception.VideoProcessingException;
import elice.aishortform.video.exception.SummaryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VideoService {

	private final VideoRepository videoRepository;
	private final WebClient webClient;
	private final SummaryRepository summaryRepository;
	private final ImageRepository imageRepository;

	@Async
	@Transactional(readOnly = true)
	public CompletableFuture<Video> generateVideo(Long summaryId) {
		return CompletableFuture.supplyAsync(() -> {
			Video video = null;
			try {
				// 1. Summary 정보 조회
				Summary summary = summaryRepository.findByIdWithParagraphImageMap(summaryId)
					.orElseThrow(() -> new SummaryNotFoundException("Summary not found: " + summaryId));

				// 2. 비디오 객체 생성 및 초기 저장 (PENDING 상태)
				video = Video.createNew(summaryId);
				video = videoRepository.save(video);
				log.info("🎬 비디오 생성 요청 시작 - Summary ID: {}, Video ID: {}", summaryId, video.getId());

				// 3. 상태를 PROCESSING으로 변경
				video.markProcessing();
				video = videoRepository.save(video);

				// 4. 요청 데이터 준비
				VideoRequest request = createVideoRequest(summary);
				
				// 요청 데이터 유효성 검증
				validateVideoRequest(request);
				
				// 요청 데이터 로깅
				logRequestData(request);

				// 5. FastAPI 서버에 비디오 생성 요청
				Video finalVideo = video;
				VideoResponse response = webClient.post()
					.uri("/video/generate")
					.bodyValue(request)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
						clientResponse.bodyToMono(String.class)
							.flatMap(errorBody -> {
								log.error("❌ FastAPI 서버 클라이언트 오류 ({}): {}", 
									clientResponse.statusCode().value(), errorBody);
								finalVideo.markFailed();
								videoRepository.save(finalVideo);
								return Mono.error(new VideoProcessingException(
									"FastAPI 서버 클라이언트 오류: " + clientResponse.statusCode().value() + " - " + errorBody));
							})
					)
					.onStatus(HttpStatusCode::is5xxServerError, serverResponse ->
						serverResponse.bodyToMono(String.class)
							.flatMap(errorBody -> {
								log.error("❌ FastAPI 서버 서버 오류 ({}): {}", 
									serverResponse.statusCode().value(), errorBody);
								finalVideo.markFailed();
								videoRepository.save(finalVideo);
								return Mono.error(new VideoProcessingException(
									"FastAPI 서버 서버 오류: " + serverResponse.statusCode().value() + " - " + errorBody));
							})
					)
					.bodyToMono(VideoResponse.class)
					.timeout(Duration.ofMinutes(10))
					.doOnError(WebClientResponseException.class, e -> {
						log.error("❌ WebClient 응답 오류: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
						finalVideo.markFailed();
						videoRepository.save(finalVideo);
					})
					.doOnError(Exception.class, e -> {
						log.error("❌ 비디오 생성 중 예외 발생: {}", e.getMessage(), e);
						finalVideo.markFailed();
						videoRepository.save(finalVideo);
					})
					.block();

				// 6. 응답 처리
				if (response == null || response.videoUrl() == null) {
					video.markFailed();
					videoRepository.save(video);
					throw new VideoProcessingException("FastAPI 서버로부터 유효한 응답을 받지 못했습니다.");
				}

				// 7. 성공 시 상태 업데이트
				video.markCompleted(response.videoUrl());
				video = videoRepository.save(video);
				log.info("✅ 비디오 생성 완료 - Video ID: {}, URL: {}", video.getId(), response.videoUrl());

				return video;

			} catch (SummaryNotFoundException e) {
				log.error("❌ 요약 정보를 찾을 수 없음: {}", e.getMessage());
				throw e;
			} catch (IllegalArgumentException e) {
				log.error("❌ 잘못된 인자 값: {}", e.getMessage());
				if (video != null) {
					video.markFailed();
					videoRepository.save(video);
				}
				throw new VideoProcessingException("비디오 생성을 위한 잘못된 인자 값: " + e.getMessage(), e);
			} catch (WebClientResponseException e) {
				log.error("❌ WebClient 응답 오류: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
				if (video != null) {
					video.markFailed();
					videoRepository.save(video);
				}
				throw new VideoProcessingException("FastAPI 서버 통신 오류: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
			} catch (VideoProcessingException e) {
				log.error("❌ 비디오 처리 오류: {}", e.getMessage());
				throw e;
			} catch (Exception e) {
				log.error("❌ 비디오 생성 중 예외 발생: {}", e.getMessage(), e);
				if (video != null) {
					video.markFailed();
					videoRepository.save(video);
				}
				throw new VideoProcessingException("비디오 생성에 실패했습니다: " + e.getMessage(), e);
			}
		}).exceptionally(ex -> {
			Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
			log.error("❌ 비동기 처리 중 예외 발생: {}", cause.getMessage(), cause);
			
			if (cause instanceof SummaryNotFoundException) {
				throw new CompletionException(cause);
			} else if (cause instanceof VideoProcessingException) {
				throw new CompletionException(cause);
			} else {
				throw new CompletionException(
					new VideoProcessingException("비디오 생성 비동기 처리 중 오류 발생: " + cause.getMessage(), cause));
			}
		});
	}

	private VideoRequest createVideoRequest(Summary summary) {
		Map<String, String> imageUrls = new HashMap<>();
		Map<Integer, String> paragraphImageMap = summary.getParagraphImageMap();
		
		if (paragraphImageMap != null) {
			for (Map.Entry<Integer, String> entry : paragraphImageMap.entrySet()) {
				String imageId = entry.getValue();
				Optional<Image> imageEntity = imageRepository.findById(imageId);
				imageEntity.ifPresent(
					entity -> imageUrls.put(String.valueOf(entry.getKey()), entity.getImageUrl()));
			}
		}

		List<String> paragraphsCopy = new ArrayList<>(summary.getParagraphs());
		
		return new VideoRequest(
			summary.getSummaryId(),
			paragraphsCopy,
			summary.getVoice(),
			imageUrls
		);
	}
	
	private void validateVideoRequest(VideoRequest request) {
		if (request.summaryId() == null) {
			throw new IllegalArgumentException("summaryId는 null일 수 없습니다");
		}
		
		if (request.paragraphs() == null || request.paragraphs().isEmpty()) {
			throw new IllegalArgumentException("paragraphs는 null이거나 비어있을 수 없습니다");
		}
		
		if (request.voice() == null || request.voice().isBlank()) {
			log.warn("⚠️ voice 설정이 없습니다. 기본값이 사용될 수 있습니다.");
		}
		
		if (request.imageUrls() == null || request.imageUrls().isEmpty()) {
			log.warn("⚠️ imageUrls가 비어있습니다. 기본 이미지가 사용될 수 있습니다.");
		}
	}
	
	private void logRequestData(VideoRequest request) {
		log.info("📤 FastAPI 서버로 전송할 요청 데이터: summaryId={}, paragraphs={}, voice={}, imageUrls={}",
			request.summaryId(),
			request.paragraphs().size(),
			request.voice(),
			request.imageUrls().size());
	}

	public List<Video> getVideosBySummaryId(Long summaryId) {
		try {
			return videoRepository.findBySummaryId(summaryId);
		} catch (Exception e) {
			log.error("❌ summaryId로 비디오 목록 조회 중 오류 발생: {}", e.getMessage(), e);
			throw new VideoProcessingException("비디오 목록 조회 실패: " + e.getMessage(), e);
		}
	}

	public Optional<Video> getVideo(Long videoId) {
		try {
			return videoRepository.findById(videoId);
		} catch (Exception e) {
			log.error("❌ videoId로 비디오 조회 중 오류 발생: {}", e.getMessage(), e);
			throw new VideoProcessingException("비디오 조회 실패: " + e.getMessage(), e);
		}
	}
}
