package elice.aishortform.video.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
public class Video {
	private final Long id;
	private final Long summaryId;
	private String videoUrl;
	private VideoStatus status;
	@JsonIgnore
	private LocalDateTime createdAt;
	
	public Video(Long id, Long summaryId, String videoUrl, VideoStatus status, LocalDateTime createdAt) {
		this.id = id;
		this.summaryId = summaryId;
		this.videoUrl = videoUrl;
		this.status = status;
		this.createdAt = createdAt;
	}
	
	public static Video createNew(Long summaryId) {
		return new Video(null, summaryId, null, VideoStatus.PENDING, LocalDateTime.now());
	}
	
	public void markProcessing() {
		this.status = VideoStatus.PROCESSING;
	}
	
	public void markCompleted(String videoUrl) {
		if (Objects.isNull(videoUrl) || videoUrl.isBlank()) {
			throw new IllegalArgumentException("비디오 URL은 null이거나 비어 있을 수 없습니다");
		}
		this.videoUrl = videoUrl;
		this.status = VideoStatus.COMPLETED;
	}
	
	public void markFailed() {
		this.status = VideoStatus.FAILED;
	}
}
