package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.dto.StudentProfileRequest;
import org.campus.campusradarbackend.dto.StudentProfileResponse;
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

    /**
     * Fetches the profile for the currently authenticated student.
     */
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        StudentProfileResponse profileResponse = studentProfileService.getStudentProfile(user);
        return ResponseEntity.ok(profileResponse);
    }

    /**
     * Creates a new profile or updates an existing one for the currently authenticated student.
     */
    @PostMapping("/profile")
    public ResponseEntity<StudentProfileResponse> createOrUpdateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestBody StudentProfileRequest request) {
        // The service handles the create/update logic and returns the correct DTO.
        StudentProfileResponse updatedProfile = studentProfileService.createOrUpdateProfile(user, request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Fetches a list of all applications submitted by the currently authenticated student.
     */
    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal User student) {
        List<ApplicationResponse> applications = applicationService.getApplicationsForStudent(student);
        return ResponseEntity.ok(applications);
    }
}

