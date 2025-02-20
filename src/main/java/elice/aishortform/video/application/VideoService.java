package elice.aishortform.video.application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import elice.aishortform.video.domain.model.Video;
import elice.aishortform.video.domain.repository.VideoRepository;
import elice.aishortform.video.dto.VideoRequest;
import elice.aishortform.video.dto.VideoResponse;
import elice.aishortform.video.exception.VideoNotFoundException;
import elice.aishortform.video.exception.VideoProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

	private final VideoRepository videoRepository;
	private final WebClient webClient;

	@Async // 비동기 처리 기능을 활성화하는 어노테이션
	public CompletableFuture<Video> generateVideo(Long summaryId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				// 1. 새로운 비디오 객체 생성 후 저장
				log.info("🎬 비디오 생성 요청 시작 - Summary ID: {}", summaryId);

				// 2. FastAPI 서버에 비디오 생성 요청
				VideoResponse response = webClient.post()
					.uri("/video/generate")  // FastAPI 서버의 엔드포인트
					.bodyValue(new VideoRequest(summaryId))
					.retrieve()
					.bodyToMono(VideoResponse.class)
					.block(); // 동기 방식으로 응답 받기

				// 3. 응답 확인
				if (response == null || response.videoUrl() == null) {
					throw new VideoProcessingException("FastAPI 서버로부터 유효한 응답을 받지 못했습니다.");
				}

				// 4. 비디오 객체 생성 후 저장
				Video video = new Video(summaryId);
				video.markCompleted(response.videoUrl());
				videoRepository.save(video);
				log.info("✅ 비디오 생성 완료 - Video URL: {}", response.videoUrl());

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

	public Video getVideo(Long videoId) {
		return videoRepository.findById(videoId).orElseThrow(() ->
			new VideoNotFoundException(videoId));
	}
}
