package elice.aishortform.video.infrastructure.persistence;

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
		VideoJpaEntity entity = new VideoJpaEntity(video.getSummaryId(), video.getStatus(), video.getVideoUrl());
		VideoJpaEntity savedEntity = videoJpaRepository.save(entity);
		return new Video(savedEntity.getId(), savedEntity.getSummaryId(), savedEntity.getVideoUrl(), savedEntity.getStatus(), savedEntity.getCreatedAt());
	}

	@Override
	public Optional<Video> findById(Long videoId) {
		return videoJpaRepository.findById(videoId)
			.map(entity -> new Video(entity.getId(), entity.getSummaryId(), entity.getVideoUrl(), entity.getStatus(), entity.getCreatedAt()));
	}
}
