package org.campus.campusradarbackend.controllers;

import org.campus.campusradarbackend.dto.AiInternshipResponse;
import org.campus.campusradarbackend.dto.AiPromptRequest;
import org.campus.campusradarbackend.dto.AiStudentResponse;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.service.AiServiceClient;
import org.campus.campusradarbackend.service.InternshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class RecommendationController {

    private final AiServiceClient aiServiceClient;
    private final InternshipService internshipService;

    @Autowired
    public RecommendationController(AiServiceClient aiServiceClient, InternshipService internshipService) {
        this.aiServiceClient = aiServiceClient;
        this.internshipService = internshipService;
    }

    /**
     * Endpoint for a logged-in student to get personalized internship recommendations.
     * It uses the student's own profile and skills as the basis for the AI search.
     */
    @GetMapping("/recommend/internships")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AiInternshipResponse>> getInternshipRecommendations(@AuthenticationPrincipal User student) {
        // Format the student's profile into a text string for the AI.
        String studentProfileText = formatStudentForRag(student);
        // Extract the student's skills to send for the strict filter.
        List<String> studentSkills = student.getStudentProfile() != null ? student.getStudentProfile().getSkills() : List.of();

        // Call the AI service with both the profile text and the skills list.
        List<AiInternshipResponse> recommendations = aiServiceClient.getInternshipRecommendations(studentProfileText, studentSkills);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Endpoint for a recruiter to get candidate recommendations for a specific internship.
     * @param internshipId The ID of the internship they want to find candidates for.
     */
    @GetMapping("/recommend/candidates/{internshipId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<AiStudentResponse>> getCandidateRecommendations(@PathVariable Long internshipId) {
        // Fetch the details of the specific internship from the database.
        InternshipPosting internship = internshipService.getInternshipById(internshipId);
        // Format the internship details into a text string for the AI.
        String internshipDescription = formatInternshipForRag(internship);
        // Extract the required skills to send for the strict filter.
        List<String> requiredSkills = internship.getRequiredSkills();

        // Call the AI service with both the description and the required skills.
        List<AiStudentResponse> recommendations = aiServiceClient.getCandidateRecommendations(internshipDescription, requiredSkills);
        return ResponseEntity.ok(recommendations);
    }

    // --- Helper methods to format your Java objects into text for the AI service ---
    private String formatStudentForRag(User student) {
        StringBuilder sb = new StringBuilder();
        sb.append("Student Profile:\n");
        sb.append("Name: ").append(student.getFirstName()).append(" ").append(student.getLastName()).append("\n");
        if (student.getStudentProfile() != null) {
            sb.append("Headline: ").append(student.getStudentProfile().getHeadline()).append("\n");
            // This line is crucial as it's what the Python service's regex looks for.
            sb.append("Skills: ").append(String.join(", ", student.getStudentProfile().getSkills())).append("\n");
        }
        return sb.toString();
    }

    private String formatInternshipForRag(InternshipPosting internship) {
        StringBuilder sb = new StringBuilder();
        sb.append("Internship Posting:\n");
        sb.append("Title: ").append(internship.getTitle()).append("\n");
        sb.append("Description: ").append(internship.getDescription()).append("\n");
        // This line is crucial for the Python service's regex.
        sb.append("Required Skills: ").append(String.join(", ", internship.getRequiredSkills())).append("\n");
        return sb.toString();
    }
}