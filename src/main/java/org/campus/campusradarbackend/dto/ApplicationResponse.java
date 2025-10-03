package org.campus.campusradarbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.campus.campusradarbackend.model.ApplicationStatus;
import org.campus.campusradarbackend.model.InternshipApplication;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {
    private Long applicationId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private Long internshipId;
    private String internshipTitle;
    private Long studentId;
    private String studentEmail;

    public static ApplicationResponse fromEntity(InternshipApplication application) {
        if (application == null) {
            return null;
        }
        return new ApplicationResponse(
                application.getId(),
                application.getStatus(),
                application.getAppliedAt(),
                application.getInternship() != null ? application.getInternship().getId() : null,
                application.getInternship() != null ? application.getInternship().getTitle() : null,
                application.getStudent() != null ? application.getStudent().getId() : null,
                application.getStudent() != null ? application.getStudent().getEmail() : null
        );
    }
}
