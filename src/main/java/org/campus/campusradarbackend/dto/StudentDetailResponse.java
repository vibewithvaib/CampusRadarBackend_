package org.campus.campusradarbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.campus.campusradarbackend.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDetailResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String rollNumber;
    private String resumeUrl;
    private boolean isEnabled;

    public static StudentDetailResponse fromEntity(User user) {
        if (user == null || user.getStudentProfile() == null) {
            return null;
        }
        return new StudentDetailResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getStudentProfile().getRollNumber(),
                user.getStudentProfile().getResumeUrl(),
                user.isEnabled()
        );
    }
}
