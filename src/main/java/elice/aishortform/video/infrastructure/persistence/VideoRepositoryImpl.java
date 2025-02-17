package elice.aishortform.video.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import elice.aishortform.video.domain.model.Video;
import elice.aishortform.video.domain.repository.VideoRepository;

@Repository
public class VideoRepositoryImpl implements VideoRepository {

	private final VideoJpaRepository videoJpaRepository;

	public VideoRepositoryImpl(VideoJpaRepository videoJpaRepository) {
		this.videoJpaRepository = videoJpaRepository;
	}

	@Override
	public Video save(Video video) {
		VideoJpaEntity entity = new VideoJpaEntity(video.getSummaryId(), video.getStatus(), video.getVideoUrl());
		videoJpaRepository.save(entity);
		return new Video(entity.getSummaryId());
	}

	@Override
	public Optional<Video> findById(Long videoId) {
		return videoJpaRepository.findById(videoId)
			.map(entity -> new Video(entity.getSummaryId()));
	}
}
