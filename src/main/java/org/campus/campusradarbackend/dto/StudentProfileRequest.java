package org.campus.campusradarbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentProfileRequest {
    private String rollNo;
    private String headline;
    private String resumeUrl;
    private List<String> skills;
}
