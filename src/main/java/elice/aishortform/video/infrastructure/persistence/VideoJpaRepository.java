package elice.aishortform.video.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoJpaRepository extends JpaRepository<VideoJpaEntity, Long> {
}
