package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.*;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.service.AdminService;
import org.campus.campusradarbackend.service.InternshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final InternshipService  internshipService;


    @GetMapping("/users/pending/students")
    public ResponseEntity<List<StudentDetailResponse>> getPendingStudents() {
        return ResponseEntity.ok(adminService.getPendingStudents());
    }


    @GetMapping("/users/pending/recruiters")
    public ResponseEntity<List<RecruiterDetailResponse>> getPendingRecruiters() {
        return ResponseEntity.ok(adminService.getPendingRecruiters());
    }


    @GetMapping("/internships/pending")
    public ResponseEntity<List<InternshipPostingResponse>> getPendingInternships() {
        return ResponseEntity.ok(adminService.getPendingInternships());
    }
    @GetMapping("/internships/{internshipId}")
    public ResponseEntity<InternshipPostingResponse> getInternshipById(@PathVariable Long internshipId) {
        System.out.println("called");
        InternshipPostingResponse response = InternshipPostingResponse.fromEntity(internshipService.getInternshipById(internshipId));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable Long userId) {
        adminService.approveUser(userId);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/internships/{internshipId}/approve")
    public ResponseEntity<Void> approveInternship(@PathVariable Long internshipId) {
        adminService.approveInternship(internshipId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<StudentDetailResponse> getStudentDetails(@PathVariable Long studentId) {
        return ResponseEntity.ok(adminService.getStudentDetails(studentId));
    }

    @GetMapping("/recruiters/{recruiterId}")
    public ResponseEntity<RecruiterDetailResponse> getRecruiterDetails(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(adminService.getRecruiterDetails(recruiterId));
    }

}
