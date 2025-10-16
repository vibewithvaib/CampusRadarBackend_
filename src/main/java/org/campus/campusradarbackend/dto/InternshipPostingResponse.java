package org.campus.campusradarbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.campus.campusradarbackend.model.InternshipPosting;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipPostingResponse {
    private Long id;
    private String title;
    private String company;
    private String description;
    private String location;
    private Integer durationInWeeks;
    private Integer stipend;
    private List<String> requiredSkills;
    private Long recruiterId;
    private String recruiterEmail;

    public static InternshipPostingResponse fromEntity(InternshipPosting posting) {
        if (posting == null) {
            return null;
        }
        return new InternshipPostingResponse(
                posting.getId(),
                posting.getTitle(),
                posting.getCompany(),
                posting.getDescription(),
                posting.getLocation(),
                posting.getDurationInWeeks(),
                posting.getStipend(),
                posting.getRequiredSkills(),
                posting.getRecruiter() != null ? posting.getRecruiter().getId() : null,
                posting.getRecruiter() != null ? posting.getRecruiter().getEmail() : null
        );
    }
}
