package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.dto.InternshipPostingResponse;
import org.campus.campusradarbackend.dto.UserResponse;
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

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/internships")
    public ResponseEntity<List<InternshipPostingResponse>> getAllInternships() {
        return ResponseEntity.ok(adminService.getAllInternships());
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(adminService.getAllApplications());
    }

    @GetMapping("/users/pending")
    public ResponseEntity<List<UserResponse>> getPendingUsers() {
        return ResponseEntity.ok(adminService.getPendingUserApprovals());
    }

    @PatchMapping("/users/{userId}/approve")
    public ResponseEntity<UserResponse> approveUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.approveUser(userId));
    }

    @GetMapping("/internships/pending")
    public ResponseEntity<List<InternshipPostingResponse>> getPendingInternships() {
        return ResponseEntity.ok(adminService.getPendingInternshipApprovals());
    }

    @PatchMapping("/internships/{internshipId}/approve")
    public ResponseEntity<InternshipPostingResponse> approveInternship(@PathVariable Long internshipId) {
        return ResponseEntity.ok(adminService.approveInternship(internshipId));
    }
}
