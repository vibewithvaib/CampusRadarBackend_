package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.*;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.service.AdminService;
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


    @GetMapping("/users/pending/students")
    public ResponseEntity<List<StudentDetailResponse>> getPendingStudents() {
        return ResponseEntity.ok(adminService.getPendingStudents());
    }


    @GetMapping("/users/pending/recruiters")
    public ResponseEntity<List<RecruiterDetailResponse>> getPendingRecruiters() {
        return ResponseEntity.ok(adminService.getPendingRecruiters());
    }


    @GetMapping("/internships/pending")
    public ResponseEntity<List<InternshipPosting>> getPendingInternships() {
        return ResponseEntity.ok(adminService.getPendingInternships());
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
}
