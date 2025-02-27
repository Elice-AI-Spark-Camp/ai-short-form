package elice.aishortform.video.presentation;

import java.util.concurrent.CompletableFuture;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import elice.aishortform.video.application.VideoService;
import elice.aishortform.video.domain.model.Video;
import elice.aishortform.video.exception.VideoNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Video API", description = "비디오 생성 및 조회 API")
@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController implements VideoApiDocs {

	private final VideoService videoService;

	@PostMapping("/generate")
	public CompletableFuture<ResponseEntity<Video>> generateVideo(@RequestParam Long summaryId) {
		log.info("📹 비디오 생성 요청 - Summary ID: {}", summaryId);
		return videoService.generateVideo(summaryId)
			.thenApply(ResponseEntity::ok)
			.exceptionally(ex -> {
				log.error("❌ 비디오 생성 요청 처리 중 오류: {}", ex.getMessage());
				throw new RuntimeException("비디오 생성 실패: " + ex.getMessage(), ex);
			});
	}

	@GetMapping("/summary/{summaryId}")
	public ResponseEntity<List<Video>> getVideosBySummaryId(@PathVariable Long summaryId) {
		log.info("📋 요약 ID로 비디오 목록 조회 - Summary ID: {}", summaryId);
		List<Video> videos = videoService.getVideosBySummaryId(summaryId);
		return ResponseEntity.ok(videos);
	}
	
	@GetMapping("/{videoId}")
	public ResponseEntity<Video> getVideo(@PathVariable("videoId") Long videoId) {
		log.info("🔍 비디오 조회 - Video ID: {}", videoId);
		Video video = videoService.getVideo(videoId)
			.orElseThrow(() -> new VideoNotFoundException(videoId));
		return ResponseEntity.ok(video);
	}
}

