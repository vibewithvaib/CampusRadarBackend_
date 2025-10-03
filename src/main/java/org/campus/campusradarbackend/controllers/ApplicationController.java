package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/apply")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/{internshipId}")
    public ResponseEntity<ApplicationResponse> applyForInternship(
            @AuthenticationPrincipal User student,
            @PathVariable Long internshipId) {

        ApplicationResponse applicationResponse = applicationService.applyForInternship(student, internshipId);
        return ResponseEntity.ok(applicationResponse);
    }
    @DeleteMapping("/revoke/{applicationId}")
    public ResponseEntity<Void> revokeApplication(@PathVariable Long applicationId, @AuthenticationPrincipal User student) throws AccessDeniedException {
        applicationService.revokeApplication(student, applicationId);
        return ResponseEntity.noContent().build();
    }
}
