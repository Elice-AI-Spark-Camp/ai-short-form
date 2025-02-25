package elice.aishortform.summary.repository;

import elice.aishortform.summary.entity.Summary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findBySummaryId(Long summaryId);

    @Query("SELECT s FROM Summary s LEFT JOIN FETCH s.paragraphImageMap WHERE s.summaryId = :summaryId")
    Optional<Summary> findByIdWithParagraphImageMap(@Param("summaryId") Long summaryId);
}
