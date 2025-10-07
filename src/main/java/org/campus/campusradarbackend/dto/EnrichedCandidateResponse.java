package org.campus.campusradarbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.campus.campusradarbackend.model.ApplicationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrichedCandidateResponse {


    private Long studentId;
    private String profileText;


    private ApplicationStatus applicationStatus;
    private Long applicationId;
}
