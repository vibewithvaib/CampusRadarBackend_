package org.campus.campusradarbackend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record AiRecommendationResponse(String recommendation) {

    /**
     * This constructor is annotated with @JsonCreator to tell the Jackson JSON library
     * exactly how to create this object from an incoming JSON.
     * @param recommendation The value from the "recommendation" key in the JSON.
     */
    @JsonCreator
    public AiRecommendationResponse(@JsonProperty("recommendation") String recommendation) {
        this.recommendation = recommendation;
    }
}
