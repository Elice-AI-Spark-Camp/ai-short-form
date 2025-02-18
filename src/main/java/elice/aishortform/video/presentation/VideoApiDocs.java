package elice.aishortform.video.presentation;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import elice.aishortform.video.domain.model.Video;
import io.swagger.v3.oas.annotations.Operation;

public interface VideoApiDocs {
	@Operation(summary = "비디오 생성", description = "주어진 summaryId를 기반으로 비디오를 생성합니다.\n"
		+ "- 요청 파라미터: summaryId (Long)\n"
		+ "- 응답: 생성된 비디오 객체\n"
		+ "- 처리 과정: 비동기 작업으로 생성 요청을 수행 후, 생성된 비디오 정보를 반환")
	@PostMapping("/generate")
	CompletableFuture<ResponseEntity<Video>> generateVideo(@RequestParam Long summaryId);

	@Operation(summary = "비디오 조회", description = "비디오 ID를 기반으로 비디오를 조회합니다.\n"
		+ "- 요청 파라미터: videoId (Long)\n"
		+ "- 응답: 비디오 객체 (존재하지 않으면 예외 발생)\n"
		+ "- 예외 처리: 존재하지 않는 비디오 ID 요청 시 VideoNotFoundException 반환")
	@GetMapping("/{videoId}")
	ResponseEntity<Video> getVideo(@PathVariable("videoId") Long videoId);
}
