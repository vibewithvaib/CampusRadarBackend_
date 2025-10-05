package org.campus.campusradarbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AiServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);
    private final WebClient webClient;
    public AiServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:5001").build();
    }
    public void ingestDocument(String text) {
        webClient.post()
                .uri("/ingest")
                .bodyValue(Map.of("text", text))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Failed to ingest document: {}", error.getMessage()))
                .subscribe();
    }
    public Mono<String> getCandidateRecommendation(String prompt) {
        return webClient.post()
                .uri("/recommend/candidates")
                .bodyValue(Map.of("prompt", prompt))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("recommendation"));
    }

    public Mono<String> getInternshipRecommendation(String prompt) {
        return webClient.post()
                .uri("/recommend/internships")
                .bodyValue(Map.of("prompt", prompt))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("recommendation"));
    }
}
