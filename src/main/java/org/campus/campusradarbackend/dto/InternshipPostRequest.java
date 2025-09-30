package org.campus.campusradarbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class InternshipPostRequest {
    private String title;
    private String description;
    private String location;
    private Integer durationInWeeks;
    private Integer stipend;
    private List<String> requiredSkills;
}
