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
}
