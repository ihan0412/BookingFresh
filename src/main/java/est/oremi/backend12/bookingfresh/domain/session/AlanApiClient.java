package est.oremi.backend12.bookingfresh.domain.session;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AlanApiClient {
    private final WebClient webClient;

    public String askAlan(String content, String clientId) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/question")
                            .queryParam("client_id", clientId)
                            .queryParam("content", content)
                            .build())
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new RuntimeException("AI 서버 응답 오류")))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            return "LLM API 호출 오류: " + e.getMessage();
        }
    }

    /**
     * Alan API - 에이전트 상태 초기화 (agent_state 리셋)
     */
    public boolean resetAlanState(String clientId) {
        try {
            webClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reset-state")
                            .queryParam("client_id", clientId)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            response -> Mono.error(new RuntimeException("⚠️ Alan 상태 초기화 실패")))
                    .toBodilessEntity()
                    .block();

            return true; // 정상 호출 시 true 반환
        } catch (Exception e) {
            System.err.println("⚠️ Alan Reset 호출 오류: " + e.getMessage());
            return false;
        }
    }
}
