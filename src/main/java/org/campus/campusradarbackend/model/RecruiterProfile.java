package org.campus.campusradarbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recruiter_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String companyName;

    @Column(length = 1024)
    private String linkedInProfile;

    private String workEmail;
}
