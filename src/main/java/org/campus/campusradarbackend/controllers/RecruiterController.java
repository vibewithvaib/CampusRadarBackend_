package org.campus.campusradarbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.InternshipPostRequest;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.service.InternshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final InternshipService internshipService;

    // This endpoint is secured for RECRUITERs.
    @PostMapping("/internships")
    public ResponseEntity<InternshipPosting> postInternship(
            @AuthenticationPrincipal User recruiter,
            @RequestBody InternshipPostRequest request) {
        InternshipPosting newPosting = internshipService.createInternship(recruiter, request);
        return ResponseEntity.ok(newPosting);
    }

    @GetMapping("/internships")
    public ResponseEntity<List<InternshipPosting>> getMyPostedInternships(@AuthenticationPrincipal User recruiter) {
        List<InternshipPosting> postings = internshipService.getInternshipsByRecruiter(recruiter);
        return ResponseEntity.ok(postings);
    }
}
