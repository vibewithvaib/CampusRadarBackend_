package org.campus.campusradarbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "internship_postings")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String location;
    private Integer durationInWeeks;
    private Integer stipend;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "internship_required_skills", joinColumns = @JoinColumn(name = "posting_id"))
    @Column(name = "skill")
    private List<String> requiredSkills;

    @Builder.Default
    private boolean isApproved = false;
}
