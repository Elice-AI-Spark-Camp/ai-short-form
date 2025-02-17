package elice.aishortform.video.infrastructure.persistence;

import java.time.LocalDateTime;

import elice.aishortform.video.domain.model.VideoStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "videos")
public class VideoJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long summaryId;
	private String videoUrl;

	@Enumerated(EnumType.STRING)
	private VideoStatus status;
	private LocalDateTime createdAt;

	protected VideoJpaEntity() {}

	public VideoJpaEntity(Long summaryId, VideoStatus status, String videoUrl) {
		this.summaryId = summaryId;
		this.status = status;
		this.videoUrl = videoUrl;
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}


}