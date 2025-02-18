package elice.aishortform.repository;

import elice.aishortform.entity.Summary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findBySummaryId(Long summaryId);
}
