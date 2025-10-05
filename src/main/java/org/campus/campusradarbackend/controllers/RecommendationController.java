package org.campus.campusradarbackend.controllers;

import org.campus.campusradarbackend.service.AiServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Mono<ResponseEntity<String>> getInternshipRecommendations(@RequestBody String prompt) {
        return aiServiceClient.getInternshipRecommendation(prompt)
                .map(ResponseEntity::ok);
    }


    @PostMapping("/recommend/candidates")
    @PreAuthorize("hasRole('RECRUITER')")
    public Mono<ResponseEntity<String>> getCandidateRecommendations(@RequestBody String prompt) {
        return aiServiceClient.getCandidateRecommendation(prompt)
                .map(ResponseEntity::ok);
    }
}
