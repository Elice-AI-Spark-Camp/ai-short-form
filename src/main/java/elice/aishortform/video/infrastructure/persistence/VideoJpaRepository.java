package elice.aishortform.video.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import elice.aishortform.video.domain.model.Video;

public interface VideoJpaRepository extends JpaRepository<VideoJpaEntity, Long> {
	List<VideoJpaEntity> findBySummaryId(Long summaryId);

}
