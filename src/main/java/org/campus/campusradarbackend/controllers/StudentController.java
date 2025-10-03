package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.dto.StudentProfileRequest;
import org.campus.campusradarbackend.dto.StudentProfileResponse;
import org.campus.campusradarbackend.model.StudentProfile;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.service.ApplicationService;
import org.campus.campusradarbackend.service.StudentProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentProfileService studentProfileService;
    private final ApplicationService applicationService;
    // This endpoint is secured by the rules in SecurityConfig. Only STUDENTs can access it.
    // NEW VERSION - Corrected with DTO

    @GetMapping("/profile")
    public ResponseEntity<StudentProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        // 1. The service method now correctly returns the DTO
        StudentProfileResponse profileResponse = studentProfileService.getStudentProfile(user);

        // 2. We return the DTO, which is a simple object that Jackson can easily handle
        return ResponseEntity.ok(profileResponse);
    }

    @PostMapping("/profile")
    public ResponseEntity<StudentProfile> createOrUpdateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestBody StudentProfileRequest request) {
        StudentProfile updatedProfile = studentProfileService.createOrUpdateProfile(user, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal User student) {
        List<ApplicationResponse> applications = applicationService.getApplicationsForStudent(student);
        return ResponseEntity.ok(applications);
    }
}