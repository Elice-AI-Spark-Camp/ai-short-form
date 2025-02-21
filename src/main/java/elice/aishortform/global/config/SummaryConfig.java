package elice.aishortform.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("elice.summary")
public class SummaryConfig {
    private String url;
    private String systemMessage;
}
