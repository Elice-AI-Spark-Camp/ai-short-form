package elice.aishortform.video.dto;

import java.util.List;
import java.util.Map;

public record VideoRequest(
    Long summaryId,
    List<String> paragraphs,     // 문단별 텍스트
    String voice,
    Map<String, String> imageUrls // 문단 인덱스 -> 이미지 URL
) {}
