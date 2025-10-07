package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.ApplicationResponse;
import org.campus.campusradarbackend.model.ApplicationStatus;
import org.campus.campusradarbackend.model.InternshipApplication;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.repository.ApplicationRepository;
import org.campus.campusradarbackend.repository.InternshipPostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final EmailService emailService; // 1. Inject the EmailService
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

        // --- UPDATED EMAIL TRIGGER POINT ---
        // Extract the required information into simple strings here,
        // while the database session is still active. This prevents lazy loading errors.
        if (newStatus == ApplicationStatus.SHORTLISTED) {
            System.out.println("Shortlisted");
            emailService.sendShortlistNotification(
                    updatedApplication.getStudent().getEmail(),
                    updatedApplication.getStudent().getFirstName(),
                    updatedApplication.getInternship().getTitle()
//                    updatedApplication.getInternship().getCompanyName()
            );
        } else if (newStatus == ApplicationStatus.REJECTED) {
            emailService.sendRejectionNotification(
                    updatedApplication.getStudent().getEmail(),
                    updatedApplication.getStudent().getFirstName(),
                    updatedApplication.getInternship().getTitle()
            );
        }
        else if (newStatus == ApplicationStatus.HIRED) {
            emailService.sendHiredNotification(
                    updatedApplication.getStudent().getEmail(),
                    updatedApplication.getStudent().getFirstName(),
                    updatedApplication.getInternship().getTitle()

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
        // 1. Fetch the internship and ensure the recruiter owns it.
        InternshipPosting internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getRecruiter().getId().equals(recruiter.getId())) {
            throw new AccessDeniedException("You are not authorized to manage this internship.");
        }

        // 2. Fetch all eligible applicants (those who have 'APPLIED').
        List<InternshipApplication> eligibleApplicants = applicationRepository.findByInternshipIdAndStatus(internshipId, ApplicationStatus.APPLIED);

        if (eligibleApplicants.isEmpty()) {
            return List.of(); // Return empty if no one has applied yet.
        }

        // 3. Format the data for the Python AI service.
        String internshipDescription = formatInternshipForRag(internship);
        List<String> applicantProfiles = eligibleApplicants.stream()
                .map(this::formatStudentForRagFromApp)
                .toList();

        // 4. Call the AI to get the IDs of the best candidates from the provided list.
        List<Integer> recommendedIds = aiServiceClient.getFilteredRecommendations(internshipDescription, applicantProfiles);

        if (recommendedIds.isEmpty()) {
            return List.of(); // Return empty if the AI found no good matches.
        }

        // Create a fast lookup map of: studentId -> their application object
        Map<Long, InternshipApplication> eligibleApplicantsMap = eligibleApplicants.stream()
                .collect(Collectors.toMap(app -> app.getStudent().getId(), app -> app));

        // 5. Update the status for each AI-recommended student and collect the results.
        return recommendedIds.stream()
                .map(id -> (long)id) // Convert Integer to Long for map key
                .filter(eligibleApplicantsMap::containsKey) // Ensure the ID is valid
                .map(eligibleApplicantsMap::get)
                .map(appToShortlist -> {
                    try {
                        // Reuse the existing update method to change status and trigger emails
                        return updateApplicationStatus(recruiter, appToShortlist.getId(), ApplicationStatus.SHORTLISTED);
                    } catch (AccessDeniedException e) {
                        // This should not happen due to the initial check, but is good practice.
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
