package elice.aishortform.video.presentation;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import elice.aishortform.video.application.VideoService;
import elice.aishortform.video.domain.model.Video;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Video API", description = "비디오 생성 및 조회 API")
@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController implements VideoApiDocs {

	private final VideoService videoService;

	@PostMapping("/generate")
	public CompletableFuture<ResponseEntity<Video>> generateVideo(@RequestParam Long summaryId) {
		return videoService.generateVideo(summaryId)
			.thenApply(ResponseEntity::ok);
	}

	@GetMapping("/{videoId}")
	public ResponseEntity<Video> getVideo(@PathVariable("videoId") Long videoId) {
		Video video = videoService.getVideo(videoId);
		return ResponseEntity.ok(video);
	}
}

