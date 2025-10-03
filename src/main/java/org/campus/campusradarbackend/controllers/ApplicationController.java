package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
