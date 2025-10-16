package org.campus.campusradarbackend.service;

import org.campus.campusradarbackend.dto.AiInternshipResponse;
import org.campus.campusradarbackend.dto.AiStudentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class AiServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);
    private final WebClient webClient;

    public AiServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:5001").build();
    }

    public void ingestDocument(String text, Map<String, Object> metadata) {
        webClient.post()
                .uri("/ingest")
                .bodyValue(Map.of("text", text, "metadata", metadata))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Failed to ingest document: {}", error.getMessage()))
                .subscribe();
    }


    public List<AiStudentResponse> getCandidateRecommendations(String internshipDescription, List<String> requiredSkills) {
        return webClient.post()
                .uri("/recommend/candidates")
                // FIX: Added required_skills to the request body
                .bodyValue(Map.of(
                        "internship_description", internshipDescription,
                        "required_skills", requiredSkills
                ))
                .retrieve()
                .bodyToFlux(AiStudentResponse.class)
                .collectList()
                .block();
    }

    /**
     * Gets a list of recommended internships for a given student profile.
     * The request body for this endpoint also needs to be updated.
     */
    public List<AiInternshipResponse> getInternshipRecommendations(String studentProfile, List<String> studentSkills) {
        System.out.println(studentProfile);
        System.out.println(studentSkills);
        return webClient.post()
                .uri("/recommend/internships")
                .bodyValue(Map.of(
                        "student_profile", studentProfile,
                        "student_skills", studentSkills
                ))
                .retrieve()
                .bodyToFlux(AiInternshipResponse.class)
                .collectList()
                .block();
    }
    public List<Integer> getFilteredRecommendations(String internshipDescription, List<String> applicantProfiles) {
        // We expect a response like {"recommended_student_ids": [101, 105]}
        // so we map it to a Map and extract the list.
        Map<String, List<Integer>> response = webClient.post()
                .uri("/recommend/candidates")
                .bodyValue(Map.of(
                        "internship_description", internshipDescription,
                        "applicant_profiles", applicantProfiles
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response.getOrDefault("recommended_student_ids", List.of());
    }


}