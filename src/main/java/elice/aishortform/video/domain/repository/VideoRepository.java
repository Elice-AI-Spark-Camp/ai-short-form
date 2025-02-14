package elice.aishortform.video.domain.repository;

import java.util.Optional;

import elice.aishortform.video.domain.model.Video;

public interface VideoRepository {
	Video save(Video video);
	Optional<Video> findById(Long id);
}
