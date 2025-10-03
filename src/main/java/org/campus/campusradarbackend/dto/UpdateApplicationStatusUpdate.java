package org.campus.campusradarbackend.dto;

import lombok.Data;
import org.campus.campusradarbackend.model.ApplicationStatus;

@Data
public class UpdateApplicationStatusUpdate {
    private ApplicationStatus applicationStatus;
}
