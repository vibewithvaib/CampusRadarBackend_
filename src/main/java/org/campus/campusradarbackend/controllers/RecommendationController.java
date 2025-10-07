package org.campus.campusradarbackend.controllers;

import org.campus.campusradarbackend.dto.*;
import org.campus.campusradarbackend.model.ApplicationStatus;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.service.AiServiceClient;
import org.campus.campusradarbackend.service.ApplicationService;
import org.campus.campusradarbackend.service.InternshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class RecommendationController {

    private final AiServiceClient aiServiceClient;
    private final InternshipService internshipService;
    private final ApplicationService applicationService;

    @Autowired
    public RecommendationController(AiServiceClient aiServiceClient, InternshipService internshipService, ApplicationService applicationService) {
        this.aiServiceClient = aiServiceClient;
        this.internshipService = internshipService;
        this.applicationService = applicationService;
    }

    @GetMapping("/recommend/internships")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AiInternshipResponse>> getInternshipRecommendations(@AuthenticationPrincipal User student) {
        String studentProfileText = formatStudentForRag(student);
        List<String> studentSkills = student.getStudentProfile() != null ? student.getStudentProfile().getSkills() : List.of();
        List<AiInternshipResponse> recommendations = aiServiceClient.getInternshipRecommendations(studentProfileText, studentSkills);
        return ResponseEntity.ok(recommendations);
    }


    @GetMapping("/recommend/candidates/{internshipId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<EnrichedCandidateResponse>> getCandidateRecommendations(@PathVariable Long internshipId, @AuthenticationPrincipal User recruiter) throws AccessDeniedException {

        InternshipPosting internship = internshipService.getInternshipById(internshipId);
        String internshipDescription = formatInternshipForRag(internship);
        List<String> requiredSkills = internship.getRequiredSkills();

        List<AiStudentResponse> aiRecommendations = aiServiceClient.getCandidateRecommendations(internshipDescription, requiredSkills);

        List<ApplicationResponse> actualApplications = applicationService.getApplicationsForInternship(recruiter, internshipId);

        Map<Long, ApplicationResponse> applicationMap = actualApplications.stream()
                .collect(Collectors.toMap(ApplicationResponse::getStudentId, app -> app));

        List<EnrichedCandidateResponse> enrichedResponse = aiRecommendations.stream().map(aiStudent -> {
            Long studentId = aiStudent.getStudentId();
            ApplicationStatus status = ApplicationStatus.valueOf("NOT_APPLIED");
            Long applicationId = null;

            if (applicationMap.containsKey(studentId)) {
                ApplicationResponse application = applicationMap.get(studentId);
                status = application.getStatus();
                applicationId = application.getApplicationId();
            }

            return new EnrichedCandidateResponse(studentId, aiStudent.getProfileText(), status, applicationId);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(enrichedResponse);
    }

    private String formatStudentForRag(User student) {
        StringBuilder sb = new StringBuilder();
        sb.append("Student Profile:\n");
        sb.append("Name: ").append(student.getFirstName()).append(" ").append(student.getLastName()).append("\n");
        if (student.getStudentProfile() != null) {
            sb.append("Headline: ").append(student.getStudentProfile().getHeadline()).append("\n");
            sb.append("Skills: ").append(String.join(", ", student.getStudentProfile().getSkills())).append("\n");
        }
        return sb.toString();
    }

    private String formatInternshipForRag(InternshipPosting internship) {
        StringBuilder sb = new StringBuilder();
        sb.append("Internship Posting:\n");
        sb.append("Title: ").append(internship.getTitle()).append("\n");
        sb.append("Description: ").append(internship.getDescription()).append("\n");
        sb.append("Required Skills: ").append(String.join(", ", internship.getRequiredSkills())).append("\n");
        return sb.toString();
    }
}
