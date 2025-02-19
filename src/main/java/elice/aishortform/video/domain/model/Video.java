package elice.aishortform.video.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Video {
	private Long id;
	private Long summaryId;
	private String videoUrl;
	private VideoStatus status;
	private LocalDateTime createdAt;

	public Video(Long summaryId) {
		this.summaryId = summaryId;
		this.status = VideoStatus.PENDING;
		this.createdAt = LocalDateTime.now();
	}

	public void markProcessing() {
		this.status = VideoStatus.PROCESSING;
	}

	public void markCompleted(String videoUrl) {
		if (Objects.isNull(videoUrl) || videoUrl.isBlank()) {
			throw new IllegalArgumentException("비디오 URL은 null이거나 비어 있을 수 없습니다");
		}
		this.status = VideoStatus.COMPLETED;
		this.videoUrl = videoUrl;
	}

	public void markFailed() {
		this.status = VideoStatus.FAILED;
	}
}
