package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.*;
import org.campus.campusradarbackend.model.InternshipApplication;
import org.campus.campusradarbackend.model.InternshipPosting;
import org.campus.campusradarbackend.model.Role;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.repository.ApplicationRepository;
import org.campus.campusradarbackend.repository.InternshipPostingRepository;
import org.campus.campusradarbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final InternshipPostingRepository internshipPostingRepository;
    private final AiServiceClient aiServiceClient; // Inject the new client



    @Transactional
    public UserResponse approveUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        User approvedUser = userRepository.save(user);

        // --- INGESTION POINT FOR STUDENTS ---
        // If the approved user is a student, format their profile and send it to the AI service.
        if (approvedUser.getRole() == Role.STUDENT) {
            String studentText = formatStudentForRag(approvedUser);
            // Create metadata map with ID and type for the Python service
            Map<String, Object> metadata = Map.of("type", "student", "id", approvedUser.getId());
            aiServiceClient.ingestDocument(studentText, metadata);
        }
        return UserResponse.fromEntity(approvedUser);
    }

    @Transactional
    public InternshipPosting approveInternship(Long internshipId) {
        InternshipPosting internship = internshipPostingRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        internship.setApproved(true);
        InternshipPosting approvedInternship = internshipPostingRepository.save(internship);

        // --- INGESTION POINT FOR INTERNSHIPS ---
        // Format the internship posting and send it to the AI service.
        String internshipText = formatInternshipForRag(approvedInternship);
        // Create metadata map with ID and type
        Map<String, Object> metadata = Map.of("type", "internship", "id", approvedInternship.getId());
        aiServiceClient.ingestDocument(internshipText, metadata);

        return approvedInternship;
    }

    // --- Helper methods to format your Java objects into text for the AI service ---
    private String formatStudentForRag(User student) {
        StringBuilder sb = new StringBuilder();
        sb.append("Student Profile:\n");
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

    // --- Other existing admin methods ---
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    public List<StudentDetailResponse> getPendingStudents() {
        return userRepository.findByIsEnabledFalse().stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .map(StudentDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RecruiterDetailResponse> getPendingRecruiters() {
        return userRepository.findByIsEnabledFalse().stream()
                .filter(user -> user.getRole() == Role.RECRUITER)
                .map(RecruiterDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InternshipPosting> getPendingInternships() {
        return internshipPostingRepository.findByisApprovedFalse();
    }
}