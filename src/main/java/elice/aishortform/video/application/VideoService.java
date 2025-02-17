package elice.aishortform.video.application;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import elice.aishortform.video.domain.model.Video;
import elice.aishortform.video.domain.repository.VideoRepository;

@Service
public class VideoService {

	private final VideoRepository videoRepository;

	public VideoService(VideoRepository videoRepository) {
		this.videoRepository = videoRepository;
	}

	@Async // 비동기 처리 기능을 활성화하는 어노테이션
	public CompletableFuture<Video> generateVideo(Long summaryId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Video video = new Video(summaryId);
				videoRepository.save(video);
				return video;
			} catch (Exception e) {
				throw new RuntimeException("비디오 생성에 실패했습니다.", e);
			}
		}).exceptionally(ex -> {
			System.err.println("예외 발생: " + ex.getMessage());
			return null;
		});
	}

	public Video getVideo(Long videoId) {
		return videoRepository.findById(videoId).orElseThrow(() ->
			new VideoNotFoundException(videoId));
	}
}
