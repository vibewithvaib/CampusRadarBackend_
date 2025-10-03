package org.campus.campusradarbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.campus.campusradarbackend.model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDetailResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String companyName;
    private String linkedInProfile;
    private String workEmail;
    private boolean isEnabled;

    public static RecruiterDetailResponse fromEntity(User user) {
        if (user == null || user.getRecruiterProfile() == null) {
            return null;
        }
        return new RecruiterDetailResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRecruiterProfile().getCompanyName(),
                user.getRecruiterProfile().getLinkedInProfile(),
                user.getRecruiterProfile().getWorkEmail(),
                user.isEnabled()
        );
    }
}
