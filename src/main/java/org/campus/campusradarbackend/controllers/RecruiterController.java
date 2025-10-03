package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.dto.InternshipPostRequest;
import org.campus.campusradarbackend.dto.InternshipPostingResponse;
import org.campus.campusradarbackend.dto.UpdateApplicationStatusUpdate;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.service.ApplicationService;
import org.campus.campusradarbackend.service.InternshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final InternshipService internshipService;
    private final ApplicationService applicationService;
    // This endpoint is secured for RECRUITERs.
    @PostMapping("/internships")
    public ResponseEntity<InternshipPosting> postInternship(
            @AuthenticationPrincipal User recruiter,
            @RequestBody InternshipPostRequest request) {
        InternshipPosting newPosting = internshipService.createInternship(recruiter, request);
        return ResponseEntity.ok(newPosting);
    }

    @GetMapping("/internships")
    public ResponseEntity<List<InternshipPostingResponse>> getMyPostedInternships(@AuthenticationPrincipal User recruiter) {
        // The service now returns a list of DTOs, which we can safely return here.
        List<InternshipPostingResponse> postingResponses = internshipService.getInternshipsByRecruiter(recruiter);
        return ResponseEntity.ok(postingResponses);
    }

    @GetMapping("/internships/{internshipId}/applications")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForInternship(
            @AuthenticationPrincipal User recruiter,
            @PathVariable Long internshipId) throws AccessDeniedException {
        List<ApplicationResponse> applications = applicationService.getApplicationsForInternship(recruiter, internshipId);
        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody UpdateApplicationStatusUpdate request,
            @AuthenticationPrincipal User recruiter) throws AccessDeniedException {
        ApplicationResponse updatedApplication = applicationService.updateApplicationStatus(recruiter, applicationId, request.getApplicationStatus());
        return ResponseEntity.ok(updatedApplication);
    }
}
