package elice.aishortform.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "summary")
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id", nullable = false)
    private Long summaryId;

    @Column(name = "summary_text", nullable = false, columnDefinition = "TEXT")
    private String summaryText;

    @ElementCollection
    @CollectionTable(name = "summary_paragraphs", joinColumns = @JoinColumn(name = "summary_id"))
    @Column(name = "paragraph", columnDefinition = "TEXT")
    @OrderColumn(name = "paragraph_index")
    private List<String> paragraphs;

    @ElementCollection
    @CollectionTable(name = "summary_images", joinColumns = @JoinColumn(name = "summary_id"))
    @MapKeyColumn(name = "paragraph_index")
    @Column(name = "image_id")
    private Map<Integer, String> paragraphImageMap;

    @Column(name = "platform", nullable = false, length = 50)
    private String platform;

}
