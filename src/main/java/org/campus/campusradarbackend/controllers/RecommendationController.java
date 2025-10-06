package org.campus.campusradarbackend.controllers;

import org.campus.campusradarbackend.dto.AiPromptRequest;
import org.campus.campusradarbackend.service.AiServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
public class RecommendationController {

    private final AiServiceClient aiServiceClient;

    @Autowired
    public RecommendationController(AiServiceClient aiServiceClient) {
        this.aiServiceClient = aiServiceClient;
    }

    @PostMapping("/recommend/internships")
    @PreAuthorize("hasRole('STUDENT')")
    // FIX: Changed return type from Mono<ResponseEntity<String>> to ResponseEntity<String>
    public ResponseEntity<String> getInternshipRecommendations(@RequestBody AiPromptRequest request) {
        // The service call is now a simple, direct call.
        String recommendation = aiServiceClient.getInternshipRecommendation(request.getPrompt());
        return ResponseEntity.ok(recommendation);
    }

    @PostMapping("/recommend/candidates")
    @PreAuthorize("hasRole('RECRUITER')")
    // FIX: Changed return type from Mono<ResponseEntity<String>> to ResponseEntity<String>
    public ResponseEntity<String> getCandidateRecommendations(@RequestBody AiPromptRequest request) {
        String recommendation = aiServiceClient.getCandidateRecommendation(request.getPrompt());
        return ResponseEntity.ok(recommendation);
    }
}