package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.model.ApplicationStatus;
import org.campus.campusradarbackend.model.InternshipApplication;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.repository.ApplicationRepository;
import org.campus.campusradarbackend.repository.InternshipPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // This Lombok annotation creates the constructor for all final fields automatically.
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final InternshipPostingRepository internshipRepository;
    private final EmailService emailService;
    private final AiServiceClient aiServiceClient;

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

    @Transactional
    public ApplicationResponse updateApplicationStatus(User recruiter, Long applicationId, ApplicationStatus newStatus) throws AccessDeniedException {
        InternshipApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        if (!application.getInternship().getRecruiter().getId().equals(recruiter.getId())) {
            throw new AccessDeniedException("You are not authorized to update this application.");
        }

        application.setStatus(newStatus);
        InternshipApplication updatedApplication = applicationRepository.save(application);

        // --- EMAIL TRIGGER POINT ---
        if (newStatus == ApplicationStatus.SHORTLISTED) {
            emailService.sendShortlistNotification(
                    updatedApplication.getStudent().getEmail(),
                    updatedApplication.getStudent().getFirstName(),
                    updatedApplication.getInternship().getTitle(),
                    updatedApplication.getInternship().getCompany()
            );
        } else if (newStatus == ApplicationStatus.REJECTED) {
            emailService.sendRejectionNotification(
                    updatedApplication.getStudent().getEmail(),
                    updatedApplication.getStudent().getFirstName(),
                    updatedApplication.getInternship().getTitle(),
                    updatedApplication.getInternship().getCompany()
            );
        } else if (newStatus == ApplicationStatus.HIRED) {
            emailService.sendHiredNotification(
                    updatedApplication.getStudent().getEmail(),
                    updatedApplication.getStudent().getFirstName(),
                    updatedApplication.getInternship().getTitle(),
                    updatedApplication.getInternship().getCompany()
            );
        }

        return ApplicationResponse.fromEntity(updatedApplication);
    }

    @Transactional
    public void revokeApplication(User student, Long applicationId) throws AccessDeniedException {
        InternshipApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        if (!application.getStudent().getId().equals(student.getId())) {
            throw new AccessDeniedException("You are not authorized to revoke this application.");
        }

        applicationRepository.delete(application);
    }

    @Transactional
    public List<ApplicationResponse> shortlistRecommendedApplicants(User recruiter, Long internshipId) throws AccessDeniedException {
        InternshipPosting internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getRecruiter().getId().equals(recruiter.getId())) {
            throw new AccessDeniedException("You are not authorized to manage this internship.");
        }

        List<InternshipApplication> eligibleApplicants = applicationRepository.findByInternshipIdAndStatus(internshipId, ApplicationStatus.APPLIED);

        if (eligibleApplicants.isEmpty()) {
            return List.of();
        }

        String internshipDescription = formatInternshipForRag(internship);
        List<String> applicantProfiles = eligibleApplicants.stream()
                .map(this::formatStudentForRagFromApp)
                .toList();

        List<Integer> recommendedIds = aiServiceClient.getFilteredRecommendations(internshipDescription, applicantProfiles);

        if (recommendedIds.isEmpty()) {
            return List.of();
        }

        Map<Long, InternshipApplication> eligibleApplicantsMap = eligibleApplicants.stream()
                .collect(Collectors.toMap(app -> app.getStudent().getId(), app -> app));

        return recommendedIds.stream()
                .map(id -> (long)id)
                .filter(eligibleApplicantsMap::containsKey)
                .map(eligibleApplicantsMap::get)
                .map(appToShortlist -> {
                    try {
                        return updateApplicationStatus(recruiter, appToShortlist.getId(), ApplicationStatus.SHORTLISTED);
                    } catch (AccessDeniedException e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    // --- Helper methods to format text for the AI ---
    private String formatStudentForRagFromApp(InternshipApplication app) {
        User student = app.getStudent();
        StringBuilder sb = new StringBuilder();
        sb.append("Student Profile:\n");
        sb.append("ID: ").append(student.getId()).append("\n");
        sb.append("Name: ").append(student.getFirstName()).append(" ").append(student.getLastName()).append("\n");
        if (student.getStudentProfile() != null) {
            sb.append("Headline: ").append(student.getStudentProfile().getHeadline()).append("\n");
            sb.append("Skills: ").append(String.join(", ", student.getStudentProfile().getSkills())).append("\n");
        }
        return sb.toString();
    }
    private String formatInternshipForRag(InternshipPosting internship) {
        StringBuilder sb = new StringBuilder();
        sb.append("Internship Posting:\n");
        sb.append("Title: ").append(internship.getTitle()).append("\n");
        sb.append("Description: ").append(internship.getDescription()).append("\n");
        sb.append("Required Skills: ").append(String.join(", ", internship.getRequiredSkills())).append("\n");
        return sb.toString();
    }
}

