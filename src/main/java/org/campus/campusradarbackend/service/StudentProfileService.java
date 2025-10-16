package org.campus.campusradarbackend.service;

import lombok.RequiredArgsConstructor;
import org.campus.campusradarbackend.dto.StudentDetailResponse;
import org.campus.campusradarbackend.dto.StudentProfileRequest;
import org.campus.campusradarbackend.dto.StudentProfileResponse;
import org.campus.campusradarbackend.model.Role;
import org.campus.campusradarbackend.model.StudentProfile;
import org.campus.campusradarbackend.model.User;
import org.campus.campusradarbackend.repository.StudentProfileRepository;
import org.campus.campusradarbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final AiServiceClient aiServiceClient;
    private final UserRepository userRepository;

    public StudentProfileResponse getStudentProfile(User user) {
        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElse(new StudentProfile(user)); // Return a new, empty profile if none exists
        return StudentProfileResponse.fromEntity(profile);
    }

    @Transactional
    public StudentProfileResponse createOrUpdateProfile(User user, StudentProfileRequest request) {
        // This logic finds an existing profile or creates a new one if it doesn't exist.
        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> new StudentProfile(user));

        // Update the profile fields from the request.
        profile.setHeadline(request.getHeadline());
        profile.setResumeUrl(request.getResumeUrl());
        profile.setSkills(request.getSkills());
        profile.setRollNumber(request.getRollNo());

        StudentProfile updatedProfile = studentProfileRepository.save(profile);

        // --- RE-INGESTION POINT ---
        // After saving the updated profile to our main database,
        // send the new data to the Python AI service to keep its memory fresh.
        String studentText = formatStudentForRag(updatedProfile.getUser());
        Map<String, Object> metadata = Map.of("type", "student", "id", updatedProfile.getUser().getId());
        aiServiceClient.ingestDocument(studentText, metadata);

        // Convert the updated entity to a DTO before returning to the controller.
        return StudentProfileResponse.fromEntity(updatedProfile);
    }

    // Helper method to format the student's profile into text for the AI service
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
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentDetailsById(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        // Ensure the user being fetched is actually a student
        if (user.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("User with the given ID is not a student.");
        }

        return StudentProfileResponse.fromEntity(user.getStudentProfile());
    }
}