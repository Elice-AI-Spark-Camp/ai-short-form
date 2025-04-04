package elice.aishortform.summary.repository;

import elice.aishortform.summary.entity.Summary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findBySummaryId(Long summaryId);

    @Query("SELECT s FROM Summary s LEFT JOIN FETCH s.paragraphImageMap LEFT JOIN FETCH s.paragraphs WHERE s.summaryId = :summaryId")
    Optional<Summary> findByIdWithParagraphImageMap(@PathVariable Long summaryId);

    @Query(value = "SELECT s.* FROM summary s " +
            "JOIN summary_images si ON s.summary_id = si.summary_id " +
            "WHERE si.image_id = :imageId", nativeQuery = true)
    Optional<Summary> findByImageId(@Param("imageId") String imageId);
}
