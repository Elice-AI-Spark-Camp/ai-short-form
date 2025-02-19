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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "videos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long summaryId;
	private String videoUrl;

	@Enumerated(EnumType.STRING)
	private VideoStatus status;
	private LocalDateTime createdAt;

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