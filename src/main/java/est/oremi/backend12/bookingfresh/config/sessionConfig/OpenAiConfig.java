package est.oremi.backend12.bookingfresh.config.sessionConfig;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.project-id:}")
    private String projectId;

    @Value("${ai.openai.org-id:}")
    private String orgId;

    @Bean
    public OpenAIClient openAiClient() {
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .project(projectId)
                .organization(orgId)
                .build();
    }
}

