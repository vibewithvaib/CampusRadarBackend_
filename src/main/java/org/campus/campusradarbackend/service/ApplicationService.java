package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.model.InternshipApplication;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.repository.ApplicationRepository;
import org.campus.campusradarbackend.repository.InternshipPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final InternshipPostingRepository internshipRepository;

    @Transactional
    public ApplicationResponse applyForInternship(User student, Long internshipId) {

        InternshipPosting internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found with id: " + internshipId));


        if (applicationRepository.existsByStudentIdAndInternshipId(student.getId(), internshipId)) {
            throw new IllegalStateException("You have already applied for this internship.");
        }


        InternshipApplication newApplication = new InternshipApplication();
        newApplication.setStudent(student);
        newApplication.setInternship(internship);
        InternshipApplication savedApplication = applicationRepository.save(newApplication);
        return ApplicationResponse.fromEntity(savedApplication);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForStudent(User student) {
        List<InternshipApplication> applications = applicationRepository.findByStudentId(student.getId());
        return applications.stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForInternship(User recruiter, Long internshipId) throws AccessDeniedException {
        // Security Check: Ensure the recruiter owns this internship
        InternshipPosting posting = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found with ID: " + internshipId));

        if (!posting.getRecruiter().getId().equals(recruiter.getId())) {
            throw new AccessDeniedException("You are not authorized to view applications for this internship.");
        }

        List<InternshipApplication> applications = applicationRepository.findByInternshipId(internshipId);
        return applications.stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

}
