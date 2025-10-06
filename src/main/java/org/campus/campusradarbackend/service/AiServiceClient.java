package org.campus.campusradarbackend.service;

import org.campus.campusradarbackend.dto.AiPromptRequest;
import org.campus.campusradarbackend.dto.AiRecommendationResponse;
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
    public String getCandidateRecommendation(String prompt) {
        AiPromptRequest request = new AiPromptRequest(prompt);
        return webClient.post()
                .uri("/recommend/candidates")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiRecommendationResponse.class)
                .map(AiRecommendationResponse::recommendation)
                .block(); // Add .block() to wait for the response
    }

    // FIX: Changed return type from Mono<String> to String
    public String getInternshipRecommendation(String prompt) {
        AiPromptRequest request = new AiPromptRequest(prompt);
        return webClient.post()
                .uri("/recommend/internships")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiRecommendationResponse.class)
                .map(AiRecommendationResponse::recommendation)
                .block(); // Add .block() to wait for the response
    }
}
