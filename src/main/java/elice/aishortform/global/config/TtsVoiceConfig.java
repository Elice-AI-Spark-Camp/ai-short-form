package elice.aishortform.global.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "elice.tts")
public class TtsVoiceConfig {
    private Map<String, String> voices;
}
