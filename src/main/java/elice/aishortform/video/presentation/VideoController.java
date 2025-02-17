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

@RestController
@RequestMapping("/videos")
public class VideoController {

	private final VideoService videoService;

	public VideoController(VideoService videoService) {
		this.videoService = videoService;
	}

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

