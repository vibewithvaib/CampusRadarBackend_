package org.campus.campusradarbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.campus.campusradarbackend.model.StudentProfile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {

    private Long id;
    private String headline;
    private String resumeUrl;
    private List<String> skills;
    private Long userId;
    private String userEmail;

    // A static factory method to easily convert an entity to a DTO
    public static StudentProfileResponse fromEntity(StudentProfile profile) {
        if (profile == null) {
            return null;
        }
        return new StudentProfileResponse(
                profile.getId(),
                profile.getHeadline(),
                profile.getResumeUrl(),
                profile.getSkills(),
                profile.getUser() != null ? profile.getUser().getId() : null,
                profile.getUser() != null ? profile.getUser().getEmail() : null
        );
    }
}
