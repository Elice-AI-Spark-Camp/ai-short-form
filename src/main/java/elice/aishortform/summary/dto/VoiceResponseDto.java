package elice.aishortform.summary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VoiceResponseDto {
    @Schema(description = "응답 메시지", example = "음성 선택 완료")
    private String message;
}
