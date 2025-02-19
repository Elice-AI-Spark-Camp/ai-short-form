package elice.aishortform.summary.repository;

import elice.aishortform.summary.entity.Summary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findBySummaryId(Long summaryId);
}
