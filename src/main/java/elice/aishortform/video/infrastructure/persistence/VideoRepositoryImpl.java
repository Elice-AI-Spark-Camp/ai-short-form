package elice.aishortform.video.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import elice.aishortform.video.domain.model.Video;
import elice.aishortform.video.domain.repository.VideoRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VideoRepositoryImpl implements VideoRepository {

	private final VideoJpaRepository videoJpaRepository;

	@Override
	public Video save(Video video) {
		VideoJpaEntity entity = toEntity(video);
		VideoJpaEntity savedEntity = videoJpaRepository.save(entity);
		return toDomain(savedEntity);
	}

	@Override
	public Optional<Video> findById(Long videoId) {
		return videoJpaRepository.findById(videoId)
			.map(this::toDomain);
	}

	@Override
	public List<Video> findBySummaryId(Long summaryId) {
		return videoJpaRepository.findBySummaryId(summaryId)
			.stream()
			.map(this::toDomain)
			.toList();
	}

	private VideoJpaEntity toEntity(Video video) {
		return new VideoJpaEntity(
			video.getSummaryId(),
			video.getStatus(),
			video.getVideoUrl()
		);
	}

	private Video toDomain(VideoJpaEntity entity) {
		return new Video(
			entity.getId(),
			entity.getSummaryId(),
			entity.getVideoUrl(),
			entity.getStatus(),
			entity.getCreatedAt()
		);
	}
}
